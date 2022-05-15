package com.github.megbailey.gsheets.database;

import com.github.megbailey.gsheets.api.GSpreadsheet;
import org.apache.commons.text.StringEscapeUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class GSheetsSQLExecutor {
    private static GSheetsLogHandler logger = new GSheetsLogHandler();
    private GSpreadsheet spreadsheet;

    public GSheetsSQLExecutor(String spreadsheetID) throws GeneralSecurityException, IOException {
        this.spreadsheet = new GSpreadsheet(spreadsheetID);
    }


    public void execute(String encodedAttempt) throws JSQLParserException {
        String decodedAttempt = StringEscapeUtils.unescapeHtml3(encodedAttempt);
        Statements statements = CCJSqlParserUtil.parseStatements(decodedAttempt);

        List<Statement> statementsList = statements.getStatements();
        Iterator<Statement> iterator = statementsList.iterator();

        //https://blog.knoldus.com/parse-database-query-with-jsql-parser/
        while (iterator.hasNext()) {
            Statement sqlStatement = iterator.next();

            //List of implemented GSheets statements -> JSqlParser has more
            if (sqlStatement instanceof Select) {
                this.executeSelect((Select) sqlStatement);
            } else if (sqlStatement instanceof Insert) {
                this.executeInsert((Insert) sqlStatement);
            } else if (sqlStatement instanceof Update) {
                this.executeUpdate((Update) sqlStatement);
            } else if (sqlStatement instanceof Delete) {
                this.executeDelete((Delete) sqlStatement);
            } else {
                LogRecord unsupported = new LogRecord(Level.WARNING, "Unsupported SQL statement: " + sqlStatement);
                logger.publish(unsupported);
            }
        }
    }

    private void executeSelect(Select select) throws GSheetsSQLException {
        SelectBody body = select.getSelectBody();

        if (body instanceof PlainSelect) {

            PlainSelect plainSelect = (PlainSelect) body;
            String fromItem = plainSelect.getFromItem().toString();
            Integer sheetID = this.spreadsheet.getSheetID(fromItem);

            if (sheetID == null) {
                throw new GSheetsSQLException("NO FROM");
            }
            System.out.println("from: " + sheetID.toString() + " --> " + fromItem.toString());
            String gVizQuery = this.spreadsheet.buildQuery(plainSelect.getSelectItems(), fromItem);
            System.out.println("query: " + select.toString() +  " --> " + gVizQuery);
            this.spreadsheet.executeGViz(gVizQuery, sheetID);

        } else {
            logger.publish(new LogRecord(Level.WARNING, "Unsupported select: " + body.toString()));
            throw new GSheetsSQLException("Unsupported select: " + body.toString());
        }

    }

    private static void executeInsert(Insert insert) {
    }

    private static void executeUpdate(Update update) {
    }

    private static void executeDelete(Delete delete) {
    }



    public static void main(String[] args) {

        try {
            GSheetsSQLExecutor executor = new GSheetsSQLExecutor("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");
            //JsonArray response = spreadsheet.executeQuery("select%20C,%20D", 1113196762);

            executor.execute("SELECT my_column FROM class");
            //executor.execute("SELECT some.hi sheet.my_column");
            //executor.execute("SELECT some.u sheet.my_column where this = that AND somethis = somethat");

            /*
            GSheet classSchema = spreadsheet.getGSheets().get("class.schema");
            List<Object> columnNames = new ArrayList<>(5);
            columnNames.add("column1");
            columnNames.add("column2");
            columnNames.add("column3");
            classSchema.updateData("$A1:$C1", columnNames);

            // Sample get data

            HashMap<String, GSheet> gSheets = spreadsheet.getGSheets();
            GSheet gSheet;
            Iterator iterator  = gSheets.keySet().iterator();
            List<List<Object>> data;
            while(iterator.hasNext()) {
                gSheet = gSheets.get(iterator.next());

                //data = gSheet.getData("$A1:$C1");
                //System.out.println(gSheet.getName() + ": " + data);
            }
            */
            // Sample create sheet
            /*
            spreadsheet.createSheet("newSheet");
            List<Sheet> sheets = spreadsheet.getSheets();
            System.out.println(GSON.toJson(sheets));
            */

            // Sample delete sheet
            /*
            spreadsheet.deleteSheet("chase");
            List<Sheet> sheets = spreadsheet.getSheets();
            System.out.println(GSON.toJson(sheets));
            */
        } catch (IOException | GeneralSecurityException e ) {
            System.out.println("There was a problem accessing the spreadsheet");
            e.printStackTrace();
        } catch (JSQLParserException e) {
            System.out.println("Improper HTML SQL: ");
            e.printStackTrace();
        }
    }

}
