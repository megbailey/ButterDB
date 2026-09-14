package com.github.megbailey.butter.relation;

import com.github.megbailey.butter.ButterDBManager;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.exception.BadRequestException;
import com.github.megbailey.butter.exception.ResourceNotFoundException;
import com.github.megbailey.butter.util.ColumnLetters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manages a pivot worksheet used by BelongsToMany relationships.
 */
public class PivotTable {
    private final String sheetName;
    private final String foreignPivotKey;
    private final String relatedPivotKey;
    private final List<String> withPivotColumns = new ArrayList<>();
    private final Map<String, Object> wherePivot = new LinkedHashMap<>();
    private boolean initialized;

    public PivotTable(String sheetName, String foreignPivotKey, String relatedPivotKey) {
        this.sheetName = sheetName;
        this.foreignPivotKey = foreignPivotKey;
        this.relatedPivotKey = relatedPivotKey;
    }

    public PivotTable withPivot(String... columns) {
        withPivotColumns.addAll(Arrays.asList(columns));
        return this;
    }

    public PivotTable wherePivot(String column, Object value) {
        wherePivot.put(column, value);
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getForeignPivotKey() {
        return foreignPivotKey;
    }

    public String getRelatedPivotKey() {
        return relatedPivotKey;
    }

    public synchronized void ensureSheet() throws IOException, BadRequestException, ResourceNotFoundException {
        if (initialized) {
            return;
        }
        GSpreadsheet db = ButterDBManager.getDatabase();
        db.firstOrNewSheet(sheetName);
        List<String> headers = new ArrayList<>();
        headers.add(foreignPivotKey);
        headers.add(relatedPivotKey);
        headers.addAll(withPivotColumns);

        List<List<Object>> existing = db.getWithRange(sheetName, "A1:ZZ1");
        if (existing == null || existing.isEmpty() || existing.get(0) == null || existing.get(0).isEmpty()) {
            db.updateRow(sheetName, new ArrayList<>(headers));
        } else {
            List<Object> first = new ArrayList<>(existing.get(0));
            boolean missing = false;
            for (String header : headers) {
                if (!first.contains(header)) {
                    missing = true;
                    first.add(header);
                }
            }
            if (missing) {
                db.updateRow(sheetName, first);
            }
        }
        initialized = true;
    }

    public List<Map<String, Object>> rowsForParent(Object parentKey) throws Exception {
        return rowsForParents(List.of(parentKey));
    }

    public List<Map<String, Object>> rowsForParents(List<Object> parentKeys) throws Exception {
        ensureSheet();
        if (parentKeys == null || parentKeys.isEmpty()) {
            return List.of();
        }
        GSpreadsheet db = ButterDBManager.getDatabase();
        List<List<Object>> headerRows = db.getWithRange(sheetName, "A1:ZZ1");
        if (headerRows == null || headerRows.isEmpty()) {
            return List.of();
        }
        List<Object> headers = headerRows.get(0);
        List<List<Object>> rows = db.getWithRange(sheetName, "A2:ZZ");
        if (rows == null) {
            return List.of();
        }
        var keySet = parentKeys.stream().map(String::valueOf).collect(Collectors.toSet());
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<Object> row : rows) {
            Map<String, Object> map = rowToMap(headers, row);
            if (!keySet.contains(String.valueOf(map.get(foreignPivotKey)))) {
                continue;
            }
            if (!matchesWherePivot(map)) {
                continue;
            }
            result.add(map);
        }
        return result;
    }

    public void attach(Object parentKey, Object relatedKey, Map<String, Object> pivotAttributes) throws Exception {
        ensureSheet();
        if (exists(parentKey, relatedKey)) {
            return;
        }
        GSpreadsheet db = ButterDBManager.getDatabase();
        List<Object> headers = db.getWithRange(sheetName, "A1:ZZ1").get(0);
        Map<String, Object> values = new HashMap<>();
        values.put(foreignPivotKey, parentKey);
        values.put(relatedPivotKey, relatedKey);
        if (pivotAttributes != null) {
            values.putAll(pivotAttributes);
        }
        List<Object> row = new ArrayList<>();
        for (Object header : headers) {
            row.add(values.getOrDefault(header.toString(), ""));
        }
        String lastCol = ColumnLetters.toLetter(Math.max(headers.size() - 1, 0));
        db.insertRow(sheetName, "A:" + lastCol, row);
    }

    public void detach(Object parentKey, Object relatedKey) throws Exception {
        ensureSheet();
        GSpreadsheet db = ButterDBManager.getDatabase();
        List<List<Object>> headerRows = db.getWithRange(sheetName, "A1:ZZ1");
        List<List<Object>> all = db.getWithRange(sheetName, "A2:ZZ");
        if (headerRows == null || all == null) {
            return;
        }
        List<Object> headers = headerRows.get(0);
        int foreignIdx = headers.indexOf(foreignPivotKey);
        int relatedIdx = headers.indexOf(relatedPivotKey);
        List<Integer> toDelete = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            List<Object> row = all.get(i);
            String f = foreignIdx >= 0 && row.size() > foreignIdx ? String.valueOf(row.get(foreignIdx)) : "";
            String r = relatedIdx >= 0 && row.size() > relatedIdx ? String.valueOf(row.get(relatedIdx)) : "";
            if (Objects.equals(f, String.valueOf(parentKey)) && Objects.equals(r, String.valueOf(relatedKey))) {
                // Sheets API delete dimension uses 0-based indices including header row
                toDelete.add(i + 1);
            }
        }
        if (!toDelete.isEmpty()) {
            db.deleteRowsByIndex(sheetName, toDelete);
        }
    }

    public void sync(Object parentKey, List<Object> relatedKeys, Map<Object, Map<String, Object>> pivotPayload) throws Exception {
        List<Map<String, Object>> existing = rowsForParent(parentKey);
        var desired = relatedKeys.stream().map(String::valueOf).collect(Collectors.toSet());
        for (Map<String, Object> row : existing) {
            Object related = row.get(relatedPivotKey);
            if (!desired.contains(String.valueOf(related))) {
                detach(parentKey, related);
            }
        }
        for (Object relatedKey : relatedKeys) {
            Map<String, Object> attrs = pivotPayload == null ? Map.of() : pivotPayload.getOrDefault(relatedKey, Map.of());
            if (!exists(parentKey, relatedKey)) {
                attach(parentKey, relatedKey, attrs);
            }
        }
    }

    public boolean exists(Object parentKey, Object relatedKey) throws Exception {
        for (Map<String, Object> row : rowsForParent(parentKey)) {
            if (Objects.equals(String.valueOf(row.get(relatedPivotKey)), String.valueOf(relatedKey))) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesWherePivot(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : wherePivot.entrySet()) {
            if (!Objects.equals(String.valueOf(map.get(entry.getKey())), String.valueOf(entry.getValue()))) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Object> rowToMap(List<Object> headers, List<Object> row) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            Object value = i < row.size() ? row.get(i) : null;
            map.put(headers.get(i).toString(), value);
        }
        return map;
    }
}
