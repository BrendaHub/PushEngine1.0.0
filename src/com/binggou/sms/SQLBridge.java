package com.binggou.sms;

import com.binggou.sms.mission.core.about.util.ConnectionFactory;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 数据库操作封装对象， 封装公共数据库操作,提供公共的数据库操作接口
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class SQLBridge
{
//	private static Logger logger = Logger.getLogger(SQLBridge.class);
    private Connection conn = null;//数据库联接
    private Statement stmt = null;//数据库操作声明
    private PreparedStatement preparedStmt = null;//数据库操作预声明
    private CallableStatement cstmt = null;//数据库存储过程操作声明
    private ResultSet rs = null;//数据库查询结果集
    private ResultSetMetaData rsmd = null;//数据库查询结果数据描述
    private String jdbcDriver = null;//jdbc数据驱动对象名称
    private String connURL = null;//数据库连接字符串
    private String userName = null;//数据库连接用户名
    private String password = null;//数据库连接密码
    
    /**
     * 数据库操作桥构造函数
     */
    public SQLBridge()
    {
    }

    /**
     * 初始化,建立与数据库的连接
     * @param jdbcDriver jdbc驱动对象名称
     * @param connURL 数据库连接字符串
     */
    public void init(String jdbcDriver, String connURL)
    {
        //获得连接配置信息
        this.jdbcDriver = jdbcDriver;
        this.connURL = connURL;
    }
    
    /**
     * 初始化,建立与数据库的连接
     * @param jdbcDriver jdbc驱动对象名称
     * @param connURL 数据库连接字符串
     * @param userName 数据库连接用户名
     * @param password 数据库连接密码
     */
    public void init(String jdbcDriver, String connURL, String userName, String password)
    {
        //获得连接配置信息
        this.jdbcDriver = jdbcDriver;
        this.connURL = connURL;
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * 打开数据库的连接
     * @return 建立连接是否成功
     */
    public boolean openConnect()
    {
        //建立与数据库的连接
        try
        {
            System.out.println(">>>>@@@@");
//            conn = ConnectionFactory.getInstance().getConnection();
            conn = ConnectionFactory.getConnection();
            System.out.println("建立与数据库的连接 = "+ conn);
            conn.setAutoCommit(true);
        }
        catch (Exception e)
        {
//        	logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnect()
    {
        try
        {
            clearResult();
            if (conn != null && !conn.isClosed())
                conn.close();
        }
        catch (Exception e)
        {
//        	logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 关闭查询结果集
     * 
     * @throws SQLException
     */
    public void clearResult()
    {
        try
        {
            if (rs != null)
                rs.close();
            rs = null;
            if (stmt != null)
                stmt.close();
            stmt = null;
            if (preparedStmt != null)
                preparedStmt.close();
            preparedStmt = null;
            if (cstmt != null)
                cstmt.close();
            cstmt = null;
            rsmd = null;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
        }
    }

    /**
     * 执行存储过程,返回结果集
     * @param procedureCall 存储过程调用
     * @return 存储过程调用是否成功
     */
    public boolean executeProcForResulset(String procedureCall)
    {
        //判断当前连接是否有效
        if (conn == null)
            return false;
        //执行查询
        try
        {
            //判断连接是否关闭，如果关闭重新打开一个新的
            if (conn.isClosed() && !openConnect())
                return false;
            //执行查询语句
            clearResult();
            cstmt = conn.prepareCall(procedureCall);
            rs = cstmt.executeQuery();
            rsmd = rs.getMetaData();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 执行查询sql
     * @param querySql select sql 语句
     * @return 操作是否成功
     */
    public boolean executeQuery(String querySql)
    {
        //判断当前连接是否有效
        if (conn == null)
            return false;
        //执行查询
        try
        {
            //判断连接是否关闭，如果关闭重新打开一个新的
            if (conn.isClosed() && !openConnect())
                return false;
            //执行查询语句
            clearResult();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(querySql);
            rsmd = rs.getMetaData();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 执行更新sql
     * @param updateSql 更新sql
     * @return 执行更新受影响记录条数
     */
    public int executeUpdate(String updateSql)
    {
        //判断当前连接是否有效
        if (conn == null)
            return -1;
        int responseRows = 0;
        //执行查询
        try
        {
            if (conn.isClosed() && !openConnect())//判断连接是否关闭，如果关闭重新打开一个新的
                return -1;
            
            clearResult();//执行更新语句
            stmt = conn.createStatement();
            responseRows = stmt.executeUpdate(updateSql);

        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return -1;
        }
        return responseRows;
    }

    /**
     * 以事务模式，批执行sql语句
     * @param batchUpdates 批更新语句
     * @return 操作是否成功
     */
    public boolean executeBatchUpdates(String[] batchUpdates)
    {
        //判断当前连接是否有效
        if (conn == null)
            return false;
        //执行查询
        try
        {
            //判断连接是否关闭，如果关闭重新打开一个新的
            if (conn.isClosed() && !openConnect())
                return false;
            //执行更新语句
            clearResult();
            conn.setAutoCommit(false);
            for (int i = 0; i < batchUpdates.length; i++)
            {
                stmt = conn.createStatement();
                stmt.executeUpdate(batchUpdates[i]);
                stmt.close();
            }
            //提交事务处理
            conn.commit();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            //事务回滚
            try
            {
                conn.rollback();
            }
            catch (Exception ex)
            {
            	e.printStackTrace();
//            	logger.error(e.getMessage());
            }
            return false;
        }
        finally
        {
            //更改连接默认事务模式
            try
            {
                conn.setAutoCommit(true);
            }
            catch (Exception e)
            {
            	e.printStackTrace();
//            	logger.error(e.getMessage());
            }
        }
        return true;
    }

    /**
     * 装备执行更新sql
     * @param preparedUpdateSql 更新sql
     * @return 操作是否成功
     */
    public boolean prepareExecuteUpdate(String preparedUpdateSql)
    {
        //判断当前连接是否有效
        if (conn == null)
            return false;
        //装备执行查询
        try
        {
            //判断连接是否关闭，如果关闭重新打开一个新的
            if (conn.isClosed() && !openConnect())
                return false;
            //执行更新语句
            clearResult();
            preparedStmt = conn.prepareStatement(preparedUpdateSql);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 设置指定参数的整型值
     * @param paramIndex 参数索引
     * @param x 参数值
     * @throws SQLException 操作异常
     */
    public void setInt(int paramIndex, int x) throws SQLException
    {
        preparedStmt.setInt(paramIndex, x);
    }
    
    /**
     * 设置指定参数的字符串值
     * @param paramIndex 参数索引
     * @param x 参数值
     * @throws SQLException 操作异常
     */
    public void setString(int paramIndex, String x) throws SQLException
    {
        preparedStmt.setString(paramIndex, x);
    }

    /**
     * 设置指定参数的时间戳
     * @param paramIndex 参数索引
     * @param x 参数值
     * @throws SQLException 操作异常
     */
    public void setTimestamp(int paramIndex, java.sql.Timestamp x) throws SQLException
    {
        preparedStmt.setTimestamp(paramIndex, x);
    }
    
    /**
     * 设置指定参数的double型值
     * @param paramIndex 参数索引
     * @param x 参数值
     * @throws SQLException 操作异常
     */
    public void setDouble(int paramIndex, double x) throws SQLException
    {
        preparedStmt.setDouble(paramIndex, x);
    }
    
    /**
     * 设置指定参数的double型值
     * @param paramIndex 参数索引
     * @param x 参数值
     * @throws SQLException 操作异常
     */
    public void setLong(int paramIndex, long x) throws SQLException
    {
        preparedStmt.setLong(paramIndex, x);
    }
    
    /**
     * 设置指定参数的长字符串值
     * @param paramIndex 参数索引
     * @param x 参数值
     * @param length 参数长度
     * @throws SQLException
     */
    public void setAsciiStream(int paramIndex, InputStream x, int length) throws SQLException
    {
        preparedStmt.setAsciiStream(paramIndex, x, length);
    }

    /**
     * 设置指定参数的数据流
     * @param paramIndex 参数索引
     * @param x 输入流
     * @param length 流长度
     * @throws SQLException 操作异常
     */
    public void setBinaryStream(int paramIndex, InputStream x, int length) throws SQLException
    {
        preparedStmt.setBinaryStream(paramIndex, x, length);
    }

    /**
     * 执行带参数的更新sql
     * @return 执行更新受影响记录条数
     */
    public int executePreparedUpdate()
    {
        //判断当前连接是否有效
        if (conn == null)
            return -1;
        int responseRows = 0;
        //执行查询
        try
        {
            //判断连接是否关闭，如果关闭重新打开一个新的
            if (conn.isClosed() && !openConnect())
                return -1;
            if (preparedStmt == null)
                return -1;
            //执行更新语句
            responseRows = preparedStmt.executeUpdate();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return -1;
        }
        return responseRows;
    }

    /**
     * 查询结果集下条记录
     * @return 查询结果集下一条记录是否成功
     */
    public boolean next()
    {
        if (rs == null)
            return false;
        try
        {
            if (rs.next())
                return true;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * 根据查询结果集子段索引，得到当前记录对应的字段的值
     * @param column 结果集列索引
     * @param convertToString 是否转换为字符串形式
     * @return 指定列的值对象
     * @throws SQLException 数据库操作异常
     */
    private Object getField(int column, boolean convertToString) throws SQLException
    {
        //如果结果集为空，报异常
        if (rs == null || rsmd == null)
            throw new SQLException("ResultSet is null !");
        //根据字段数据类型和结果说明，放回字段的值
        switch (rsmd.getColumnType(column))
        {
        //根据数据类型取值
        case Types.BIGINT:
        {
            if (convertToString)
                return String.valueOf(rs.getLong(column));
            else
                return new Long(rs.getLong(column));
        }
        case Types.BINARY:
        {
            if (convertToString)
                return Byte.toString(rs.getByte(column));
            else
                return new Byte(rs.getByte(column));
        }
        case Types.BIT:
        {
            if (convertToString)
                return String.valueOf(rs.getBoolean(column));
            else
                return new Boolean(rs.getBoolean(column));
        }
        case Types.CHAR:
        {
            return rs.getString(column);
        }
        case Types.DATE:
        {
            if (convertToString)
                return (rs.getDate(column)).toString();
            else
                return rs.getDate(column);
        }
        case Types.DECIMAL:
        {
            if (convertToString)
                return (rs.getBigDecimal(column)).toString();
            else
                return rs.getBigDecimal(column);
        }
        case Types.DOUBLE:
        {
            if (convertToString)
                return String.valueOf(rs.getDouble(column));
            else
                return new Double(rs.getDouble(column));
        }
        case Types.FLOAT:
        {
            if (convertToString)
                return String.valueOf(rs.getFloat(column));
            else
                return new Float(rs.getFloat(column));
        }
        case Types.INTEGER:
        {
            if (convertToString)
                return String.valueOf(rs.getInt(column));
            else
                return new Integer(rs.getInt(column));
        }
        case Types.LONGVARBINARY:
        {
            if (convertToString)
                return (rs.getBinaryStream(column)).toString();
            else
                return rs.getBinaryStream(column);
        }
        case Types.LONGVARCHAR:
        {
            return rs.getString(column);
        }
        case Types.NULL:
        {
            if (convertToString)
                return "NULL";
            else
                return null;
        }
        case Types.NUMERIC:
        {
            if (convertToString)
                return (rs.getBigDecimal(column)).toString();
            else
                return rs.getBigDecimal(column);
        }
        case Types.REAL:
        {
            if (convertToString)
                return String.valueOf(rs.getFloat(column));
            else
                return new Float(rs.getFloat(column));
        }
        case Types.SMALLINT:
        {
            if (convertToString)
                return String.valueOf(rs.getShort(column));
            else
                return new Short(rs.getShort(column));
        }
        case Types.TIME:
        {
            if (convertToString)
                return (rs.getTime(column)).toString();
            else
                return rs.getTime(column);
        }
        case Types.TIMESTAMP:
        {
            if (convertToString)
                return (rs.getTimestamp(column)).toString();
            else
                return rs.getTimestamp(column);
        }
        case Types.TINYINT:
        {
            if (convertToString)
                return String.valueOf(rs.getByte(column));
            else
                return new Byte(rs.getByte(column));
        }
        case Types.VARBINARY:
        {
            if (convertToString)
                return (rs.getBytes(column)).toString();
            else
                return rs.getBytes(column);
        }
        case Types.VARCHAR:
        {
            return rs.getString(column);
        }
        default:
        {
            if (convertToString)
                return (rs.getObject(column)).toString();
            else
                return rs.getObject(column);
        }
        }
    }

    /**
     * 得到查询结果集的字段总数
     * @return 结果集字段总数
     */
    public int getColumnCount()
    {
        if (rsmd == null)
            return 0;
        int columnCount = 0;
        try
        {
            columnCount = rsmd.getColumnCount();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return -1;
        }
        return columnCount;
    }

    /**
     * 根据值段位置索引得到字段的值
     * @param column 字段列索引
     * @return 返回字段值对象
     */
    public Object getField(int column)
    {
        Object obj = null;
        try
        {
            obj = getField(column, false);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return null;
        }
        return obj;
    }

    /**
     * 根据值段名称索引得到字段的值
     * @param fieldName 字段名称
     * @return 字段值对象
     */
    public Object getField(String fieldName)
    {
        Object obj = null;
        try
        {
            obj = getField(rs.findColumn(fieldName), false);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return null;
        }
        return obj;
    }

    /**
     * 根据值段位置索引得到字段的值的字符串形式
     * @param column 字段索引
     * @return 字段值的字符串形式
     */
    public String getFieldString(int column)
    {
        Object obj = null;
        try
        {
            obj = getField(column, true);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return null;
        }
        return (String) obj;
    }

    /**
     * 根据值段名称索引得到字段的值的字符串形式
     * @param fieldName 字段名称
     * @return 字段值对象
     */
    public String getFieldString(String fieldName)
    {
        Object obj = null;
        try
        {
            obj = getField(rs.findColumn(fieldName), true);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return null;
        }
        return (String) obj;
    }

    /**
     * 根据值段名称索引得到字段的值的流形式
     * @param fieldName 字段名称
     * @return 针对流字段的输入流对象
     */
    public InputStream getFieldStream(String fieldName)
    {
        InputStream is = null;
        try
        {
            is = rs.getBinaryStream(fieldName);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return null;
        }
        return is;
    }

    /**
     * 根据值段名称索引得到字段的值的流形式
     * @param column 字段索引
     * @return 针对流字段的输入流对象
     */
    public InputStream getFieldStream(int column)
    {
        InputStream is = null;
        try
        {
            is = rs.getBinaryStream(column);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
//        	logger.error(e.getMessage());
            return null;
        }
        return is;
    }

	public Connection getConn() {
		return conn;
	}
	public PreparedStatement getPreparedStmt() {
		return preparedStmt;
	}
}