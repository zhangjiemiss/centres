package org.origin.centres.parse;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangjie
 * @version 2019-03-15
 * @apiNote 数据权限解析
 */
@SuppressWarnings({"ALL","unchecked"})
public class IRuleParse {
    private Pattern patternObj;
    private Pattern patternList;
    private ScriptEngine engine;

    public IRuleParse() {
        patternObj = Pattern.compile("(?<=\\$\\{)(\\S+)(?=})");
        patternList = Pattern.compile("(?<=\\$\\[\\{)(\\S+)(?=}])");
        engine = new ScriptEngineManager().getEngineByName("js");
    }

    // ================================parseJoins================================

    /**
     * 解析joins
     *
     * @param src join str
     * @return 结果
     */
    public String parseJoins(String src) {
        return this.parseJoins(src, null, null);
    }

    /**
     * 解析joins
     *
     * @param src join str
     * @return 结果
     */
    public String parseJoins(String src, Set<String> joinAlias, String keyword) {
        if (src != null && !"".equals(src) && src.toUpperCase().contains("JOIN")) {
            String joinsStr = src;
            String mainBody = "SELECT * FROM dual a ";
            try {
                src = mainBody + src;
                src = this.replaceKey(src);
                Select select = (Select) CCJSqlParserUtil.parse(src);
                //Statements statements = CCJSqlParserUtil.parseStatements(sql);
                PlainSelect selectBody = (PlainSelect) select.getSelectBody();
                List<Join> joins = selectBody.getJoins();
                if (keyword == null) keyword = "keyword";
                if (joinAlias == null) joinAlias = new HashSet<>();
                if (joins != null && joins.size() > 0) {
                    for (int i = joins.size() - 1; i >= 0; i--) {
                        int size = joinAlias.size();
                        Join join = joins.get(i);
                        if (join != null && join.getRightItem() != null &&
                                join.getRightItem().getAlias() != null &&
                                join.getRightItem().getAlias().getName() != null) {
                            String name = join.getRightItem().getAlias().getName();
                            joinAlias.add(keyword + "_&&_*_&&_" + name);
                        }
                        if (joinAlias.size() <= size) {
                            joins.remove(i);
                        }
                    }
                    selectBody.setJoins(joins);
                    String string = selectBody.toString();
                    string = this.replaceBackKey(string);
                    joinsStr = string.replaceAll(this.escapeExprSpecialWord(mainBody), "");
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return joinsStr;
        }
        return null;
    }

    /**
     * 将joinStr中的关键字替换掉
     *
     * @param joinStr joinStr
     * @return joinStr
     */
    private String replaceKey(String joinStr) {
        if (joinStr != null && !"".equals(joinStr)) {
            Matcher matcherObj = patternObj.matcher(joinStr);
            while (matcherObj.find()) {
                try {
                    String group = matcherObj.group();
                    String escape = this.escapeExprSpecialWord("${" + group + "}");
                    joinStr = joinStr.replaceAll(escape, "'\\$\\$2\\$\\$" + group + "\\$\\$1\\$\\$'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Matcher matcherList = patternList.matcher(joinStr);
            while (matcherList.find()) {
                try {
                    String group = matcherList.group();
                    String escape = this.escapeExprSpecialWord("$[{" + group + "}]");
                    joinStr = joinStr.replaceAll(escape, "'\\$\\$4\\$\\$" + group + "\\$\\$3\\$\\$'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return joinStr;
    }

    /**
     * 将joinStr中的关键字置换回来
     *
     * @param joinStr joinStr
     * @return joinStr
     */
    private String replaceBackKey(String joinStr) {
        joinStr = joinStr.replaceAll("'\\$\\$4\\$\\$", "\\$\\[\\{");
        joinStr = joinStr.replaceAll("\\$\\$3\\$\\$'", "\\}\\]");
        joinStr = joinStr.replaceAll("'\\$\\$2\\$\\$", "\\$\\{");
        joinStr = joinStr.replaceAll("\\$\\$1\\$\\$'", "\\}");
        return joinStr;
    }

    // ================================parseValues================================

    /**
     * 解析数据
     *
     * @param src  源
     * @param data 数据
     * @return 结果
     */
    public String parseValues(String src, Map<String, Object> data, Object entity, String prefix) {
        return this.parseValues(src, data, entity, prefix, this.patternObj, this.patternList, this.engine);
    }

    /**
     * 解析数据
     *
     * @param src         源
     * @param data        数据
     * @param patternObj  解析对象正则
     * @param patternList 解析数组正则
     * @return 结果
     */
    private String parseValues(String src, Map<String, Object> data, Object entity, String prefix, Pattern patternObj, Pattern patternList, ScriptEngine engine) {
        if (src != null && !"".equals(src)) {
            Matcher matcherObj = patternObj.matcher(src);
            while (matcherObj.find()) {
                try {
                    String group = matcherObj.group();
                    // System.out.println("group = " + group);
                    Object value = this.getValueByKeyword(data, entity, engine, group, prefix);
                    if (value instanceof Boolean) {
                        // 非此类解析
                        continue;
                    }
                    String escape = this.escapeExprSpecialWord("${" + group + "}");
                    if (value != null) {
                        List list = (List) value;
                        String val = "";
                        for (Object item : list) {
                            if (item != null) {
                                if (!val.equals("")) {
                                    val = val + ",";
                                }
                                val = val + item.toString();
                            }
                        }
                        val = "'" + val + "'";
                        src = src.replaceAll(escape, val);
                    } else {
                        src = src.replaceAll(escape, "NULL");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Matcher matcherList = patternList.matcher(src);
            while (matcherList.find()) {
                try {
                    String group = matcherList.group();
                    // System.out.println("group = " + group);
                    Object value = this.getValueByKeyword(data, entity, engine, group, prefix);
                    if (value instanceof Boolean) {
                        // 非此类解析
                        continue;
                    }
                    String escape = this.escapeExprSpecialWord("$[{" + group + "}]");
                    Pattern pattern = Pattern.compile(this.getCompileRegex(escape));
                    Matcher matcher = pattern.matcher(src);
                    while (matcher.find()) {
                        String group2 = matcher.group();
                        // System.out.println("group2 = " + group2);
                        if (value != null && ((List) value).size() > 0) {
                            List list = (List) value;
                            StringBuilder builder = new StringBuilder();
                            for (Object obj : list) {
                                if (obj != null) {
                                    if (!"".equals(builder.toString())) {
                                        builder.append(" OR ");
                                    } else {
                                        builder.append(" ");
                                    }
                                    String val = (String) obj;
                                    val = "'" + val + "'";
                                    builder.append(group2.replaceAll(escape, val));
                                }
                            }
                            if (!"".equals(builder.toString())) {
                                builder.append(") ");
                                builder.insert(0, " (");
                            }
                            // System.out.println("builder.toString = "+builder.toString());
                            src = src.replaceAll(this.escapeExprSpecialWord(group2), builder.toString());
                        } else {
                            src = src.replaceAll(this.escapeExprSpecialWord(group2), " FALSE");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return src;
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    private Object getValueByKeyword(Map<String, Object> data, Object entity, ScriptEngine engine, String keyword, String prefix) {
        if (keyword == null) return null;
        if (!keyword.startsWith(prefix)) return false;
        Object object = data.get(keyword);
        if (object != null) return object;
        object = this.gainValueByKeyword(entity, engine, keyword, prefix);
        if (object != null) data.put(keyword, object);
        return object;
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    private Object gainValueByKeyword(Object object, ScriptEngine engine, String keyword, String prefix) {
        if (object != null && keyword != null && !"".equals(keyword)) {
            String regexMatches = "(\\S+\\?\\S+)|(\\S+\\<\\S+)|(\\S+\\>\\S+)|(\\S+\\=\\S+)|" +
                    "(\\S+\\!\\S+)|(\\S+\\&\\S+)|(\\S+\\|\\S+)|(\\S+\\:\\S+)|" +
                    "(\\S+\\+\\S+)|(\\S+\\-\\S+)|(\\S+\\*\\S+)|(\\S+\\/\\S+)";
            if (keyword.matches(regexMatches)) {
                // 逻辑运算
                String regexSplit = "(\\?)|(\\<)|(\\>)|(\\=)|" +
                        "(\\!)|(\\&)|(\\|)|(\\:)|" +
                        "(\\+)|(\\-)|(\\*)|(\\/)";
                String[] split = keyword.split(regexSplit);
                Map<String, Object> data = new HashMap<>();
                String tempKeyword = keyword;
                List<Object> tempList = new ArrayList<>();
                for (String names : split) {
                    if (names.startsWith(prefix)) {
                        Object value = this.gainValueByNames(object, names.substring(prefix.length()), true);
                        if (value == null) {
                            tempKeyword = keyword.replaceAll(this.escapeExprSpecialWord(names), "null");
                        } else {
                            data.put(names, value);
                            tempList = (List) value;
                        }
                    }
                }
                if (tempList.size() > 0) {
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < tempList.size(); i++) {
                        String tempKeyword2 = tempKeyword;
                        for (String key : data.keySet()) {
                            Object value = data.get(key);
                            String escape = this.escapeExprSpecialWord(key);
                            Object obj = ((List) value).get(i);
                            if (obj == null) {
                                tempKeyword2 = tempKeyword2.replaceAll(escape, "null");
                            } else {
                                tempKeyword2 = tempKeyword2.replaceAll(escape, "'" + (String) obj + "'");
                            }
                        }
                        if (engine != null) {
                            try {
                                Object result = engine.eval(tempKeyword2);
                                if (result != null) {
                                    list.add(String.valueOf(result));
                                }
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (list.size() > 0) {
                        return list;
                    }
                }
            } else {
                // 普通取值
                return this.gainValueByNames(object, keyword.substring(prefix.length()), false);
            }
        }
        return null;
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    private Object gainValueByNames(Object object, String keyword, boolean keepNull) {
        if (object != null && keyword != null && !"".equals(keyword)) {
            List<Object> list = new ArrayList<>();
            list.add(object);
            String[] split = keyword.split("\\[\\]\\.");
            for (String name : split) {
                if (name != null && !"".equals(name)) {
                    try {
                        List<Object> mList = new ArrayList<>();
                        for (Object tempObj : list) {
                            if (tempObj != null) {
                                if (tempObj instanceof List) {
                                    for (Object tempListObj : (List) tempObj) {
                                        MetaObject metaObject = SystemMetaObject.forObject(tempListObj);
                                        Object obj = metaObject.getValue(name);
                                        if (!keepNull && obj == null) continue;
                                        mList.add(obj);
                                    }
                                } else {
                                    MetaObject metaObject = SystemMetaObject.forObject(tempObj);
                                    Object obj = metaObject.getValue(name);
                                    if (obj != null) mList.add(obj);
                                }
                                if (mList.size() > 0) {
                                    list = mList;
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return list;
        }
        return null;
    }

    /**
     * 获取
     *
     * @param escape 源
     * @return regex
     */
    private String getCompileRegex(String escape) {
        if (escape != null && !"".equals(escape)) {
            StringBuilder builder = new StringBuilder();
            //======================================== IN
            builder.append("(");
            builder.append("IN\\s{0,}\\(\\s{0,}");
            builder.append(escape);
            builder.append(".*\\)");
            builder.append(")");
            //======================================== FIND_IN_SET
            builder.append("|(");
            builder.append("FIND_IN_SET\\s{0,}\\(\\s{0,}");
            builder.append(escape);
            builder.append(".*?\\)");
            builder.append(")");
            //======================================== =
            builder.append("|(");
            builder.append("\\S+\\s{0,}=\\s{0,}");
            builder.append(escape);
            builder.append(")");
            //======================================== !=
            builder.append("|(");
            builder.append("\\S+\\s{0,}!=\\s{0,}");
            builder.append(escape);
            builder.append(")");
            //======================================== <=
            builder.append("|(");
            builder.append("\\S+\\s{0,}<=\\s{0,}");
            builder.append(escape);
            builder.append(")");
            //======================================== >=
            builder.append("|(");
            builder.append("\\S+\\s{0,}>=\\s{0,}");
            builder.append(escape);
            builder.append(")");
            //======================================== <
            builder.append("|(");
            builder.append("\\S+\\s{0,}<\\s{0,}");
            builder.append(escape);
            builder.append(")");
            //======================================== >
            builder.append("|(");
            builder.append("\\S+\\s{0,}>\\s{0,}");
            builder.append(escape);
            builder.append(")");
            return builder.toString();
        }
        return null;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword 字符
     * @return 新字符串
     */
    private String escapeExprSpecialWord(String keyword) {
        if (keyword != null && !"".equals(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 获取属性值
     *
     * @param object 实例
     * @param name   名称
     * @return 值
     */
    private Object getValue(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置属性值
     *
     * @param object 实例
     * @param name   名称
     */
    private void setValue(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归获取值
     *
     * @param object 实例
     * @param names  名称
     * @return 值
     */
    private Object getValues(Object object, String names) {
        if (object != null && names != null && !"".equals(names)) {
            Object tempObj = object;
            String[] split = names.split(".");
            for (String name : split) {
                if (name != null && !"".equals(name)) {
                    try {
                        Field field = object.getClass().getDeclaredField(name);
                        field.setAccessible(true);
                        tempObj = field.get(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return tempObj;
        }
        return null;
    }


    private String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                String result = first == null ? "" : first.toString();
                return result;
            } else {
                StringBuilder buf = new StringBuilder(256);
                if (first != null) {
                    buf.append(first);
                }

                while (iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }

    // ================================sqlEnhance================================

    /**
     * sql 增强
     *
     * @param sql sql
     * @return 增强结果
     */
    public String sqlEnhance(String sql, String joinStr, String whereStr) {
        String resultSql = sql;
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            //Statements statements = CCJSqlParserUtil.parseStatements(sql);
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            // 如果主表没有别名；就是这种别名为a
            FromItem fromItem = selectBody.getFromItem();
            if (fromItem != null && fromItem.getAlias() == null) {
                fromItem.setAlias(new Alias("a"));
                if (selectBody.getJoins() == null || selectBody.getJoins().size() <= 0) {
                    // 更新Column
                    List<SelectItem> selectItems = selectBody.getSelectItems();
                    if (selectItems != null && selectItems.size() > 0) {
                        for (SelectItem selectItem : selectItems) {
                            if (selectItem instanceof SelectExpressionItem) {
                                SelectExpressionItem expressionItem = (SelectExpressionItem) selectItem;
                                Expression expression = expressionItem.getExpression();
                                if (expression instanceof Column) {
                                    ((Column) expression).setTable((Table) fromItem);
                                }
                            }
                        }
                    }
                    // 更新Where
                    Expression where = selectBody.getWhere();
                    if ( where instanceof EqualsTo) {
                        EqualsTo equalsTo = (EqualsTo) where;
                        Expression leftExpression = equalsTo.getLeftExpression();
                        if (leftExpression instanceof Column) {
                            ((Column) leftExpression).setTable((Table) fromItem);
                        }
                        Expression rightExpression = equalsTo.getRightExpression();
                        if (rightExpression instanceof Column) {
                            ((Column) rightExpression).setTable((Table) fromItem);
                        }
                    }
                    // 更新OrderBy
                    List<OrderByElement> orderByElements = selectBody.getOrderByElements();
                    if (orderByElements != null && orderByElements.size() > 0) {
                        for (OrderByElement orderByElement : orderByElements) {
                            if (orderByElement != null) {
                                Expression expression = orderByElement.getExpression();
                                if (expression instanceof Column) {
                                    ((Column) expression).setTable((Table) fromItem);
                                }
                            }
                        }
                    }
                    // 更新GROUP BY
                    GroupByElement groupBy = selectBody.getGroupBy();
                    if (groupBy != null) {
                        List<Expression> groups = groupBy.getGroupByExpressions();
                        if (groups != null && groups.size() > 0) {
                            for (Expression expression : groups) {
                                if (expression instanceof Column) {
                                    ((Column) expression).setTable((Table) fromItem);
                                }
                            }
                        }
                    }
                }
            }
            // 添加 join
            if (joinStr != null && !"".equals(joinStr.trim())) {
                List<Join> joins = selectBody.getJoins();
                if (joins == null) joins = new ArrayList<>();
                joins.add(this.getJoin(joinStr));
                selectBody.setJoins(joins);
            }
            // 添加 where
            if (whereStr != null && !"".equals(whereStr.trim())) {
                Expression whereExpression = CCJSqlParserUtil.parseCondExpression(whereStr);
                if (selectBody.getWhere() == null) {
                    selectBody.setWhere(whereExpression);
                } else {
                    // plainSelect.setWhere(new OrExpression(plainSelect.getWhere(), whereExpression));
                    selectBody.setWhere(new AndExpression(selectBody.getWhere(), whereExpression));
                }
            }
            resultSql = selectBody.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSql;
    }

    /**
     * 获取JOIN
     *
     * @param joinStr 字符串
     * @return 结果
     */
    private Join getJoin(String joinStr) {
        Join join = new Join();
        //join.setOnExpression(joinExpression);
        if (joinStr.toUpperCase().startsWith("OUTER JOIN ")) {
            join.setOuter(true);
            joinStr = joinStr.substring(11);
        } else if (joinStr.toUpperCase().startsWith("RIGHT JOIN ")) {
            join.setRight(true);
            joinStr = joinStr.substring(11);
        } else if (joinStr.toUpperCase().startsWith("LEFT JOIN ")) {
            join.setLeft(true);
            joinStr = joinStr.substring(10);
        } else if (joinStr.toUpperCase().startsWith("NATURAL JOIN ")) {
            join.setNatural(true);
            joinStr = joinStr.substring(13);
        } else if (joinStr.toUpperCase().startsWith("FULL JOIN ")) {
            join.setFull(true);
            joinStr = joinStr.substring(10);
        } else if (joinStr.toUpperCase().startsWith("INNER JOIN ")) {
            join.setInner(true);
            joinStr = joinStr.substring(11);
        } else if (joinStr.toUpperCase().startsWith("SIMPLE JOIN ")) {
            join.setSimple(true);
            joinStr = joinStr.substring(12);
        } else if (joinStr.toUpperCase().startsWith("CROSS JOIN ")) {
            join.setCross(true);
            joinStr = joinStr.substring(11);
        } else if (joinStr.toUpperCase().startsWith("SEMI JOIN ")) {
            join.setSemi(true);
            joinStr = joinStr.substring(10);
        } else if (joinStr.toUpperCase().startsWith("JOIN ")) {
            joinStr = joinStr.substring(5);
        }
        join.setRightItem(new Table(joinStr));
        return join;
    }
}
