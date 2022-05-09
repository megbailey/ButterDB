package com.github.megbailey.gsheets.database;

import com.github.megbailey.gsheets.api.GSpreadsheet;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
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


    public static void execute(String attempt) throws JSQLParserException {
        Statements statements = CCJSqlParserUtil.parseStatements(attempt);

        List<Statement> statementsList = statements.getStatements();
        Iterator<Statement> iterator = statementsList.iterator();

        //https://blog.knoldus.com/parse-database-query-with-jsql-parser/
        while (iterator.hasNext()) {
            Statement sqlStatement = iterator.next();
            //List of implemented GSheets statements ->JSqlParser has more
            if (sqlStatement instanceof Select) {
                executeSelect((Select) sqlStatement);
            } else if (sqlStatement instanceof Insert) {
                executeInsert((Insert) sqlStatement);
            } else if (sqlStatement instanceof Update) {
                executeUpdate((Update) sqlStatement);
            } else if (sqlStatement instanceof Delete) {
                executeDelete((Delete) sqlStatement);
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
            String gVizQuery = translateQueryToGViz(plainSelect);
            Integer sheetID = translateLabelToID(plainSelect.getFromItem());
            executeGVizQuery(gVizQuery, sheetID);

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


    }
}
