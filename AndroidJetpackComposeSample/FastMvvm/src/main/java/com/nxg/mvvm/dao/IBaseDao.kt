package com.nxg.mvvm.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import java.lang.reflect.ParameterizedType

/**
 * 基本的DAO接口，声明封装常用的CURD方法
 *
 * @param <T> Entity type Entity注解的类
 * @param <K> Primary key (PK) type; use Void if entity does not have exactly one PK 一般是自增长的Long id
</K></T> */
interface IBaseDao<T, K> {
    /**
     * Insert an entity into the table associated with a concrete DAO.
     *
     * @param entity T The entity to insert
     * @return Long  row ID of newly inserted entity
     */
    @Insert
    fun insert(entity: T): Long

    /**
     * Insert the given entities into the table associated with a concrete DAO.
     *
     * @param entities Array<out T> The entities to insert
     * @return LongArray? row ID of newly inserted entities
    </out> */
    @Insert
    fun insert(vararg entities: T): LongArray?

    /**
     * Insert the given entities into the table associated with a concrete DAO.
     *
     * @param entities Collection<T>
     * @return LongArray? row ID of newly inserted entities
    </T> */
    @Insert
    fun insert(entities: Collection<T>?): LongArray?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(vararg entities: T): LongArray?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entities: Collection<T>?): LongArray?

    @Update
    fun update(entity: T): Int

    @Update
    fun update(vararg entities: T)

    @Update
    fun update(entities: Collection<T>?)

    @Delete
    fun delete(entity: T)

    @Delete
    fun delete(vararg entities: T)

    @Delete
    fun delete(entities: Collection<T>?)

    /**
     * execute sql
     *
     * @param sql SupportSQLiteQuery
     * @return List<T>
    </T> */
    @RawQuery
    fun rawQueryList(sql: SupportSQLiteQuery?): List<T>?

    /**
     * execute sql
     *
     * @param sql SupportSQLiteQuery
     * @return T
     */
    @RawQuery
    fun rawQuerySingle(sql: SupportSQLiteQuery?): T

    /**
     * execute sql
     *
     * @param sql SupportSQLiteQuery
     * @return long row num
     */
    @RawQuery
    fun rawQueryLine(sql: SupportSQLiteQuery?): Long
    fun loadAll(): List<T>? {
        return rawQueryList(SimpleSQLiteQuery("select * from $tableName"))
    }

    fun load(key: K): T {
        return rawQuerySingle(SimpleSQLiteQuery("select * from $tableName where id  =  $key"))
    }

    fun count(): Long {
        return rawQueryLine(SimpleSQLiteQuery("select count(1) from $tableName"))
    }

    fun deleteAll(): Long {
        return rawQueryLine(SimpleSQLiteQuery("delete from $tableName"))
    }
    //获取第一个就是泛型T的Class//System.out.println("getTableName: interface " + in);
    //System.out.println("getTableName: interface.getGenericInterfaces() " + genericInterfaces.length);
    //获取实现的接口
    /**
     * 接口默认方法，获取泛型T的SimpleName当做TableName，因此建议传入的Entity，表名称和Entity的SimpleName一致
     * 为什么不获取注解，因为反射也获取不到。。。所以，十分建议重写此getTableName返回实际的表名
     *
     * @return 返回泛型T的SimpleName当做TableName
     */
    val tableName: String
        get() {
            //获取实现的接口
            val interfaces = this.javaClass.interfaces
            for (`in` in interfaces) {
                //System.out.println("getTableName: interface " + in);
                val genericInterfaces = `in`.genericInterfaces
                //System.out.println("getTableName: interface.getGenericInterfaces() " + genericInterfaces.length);
                for (type in genericInterfaces) {
                    println("getTableName: type $type")
                    val actualTypeArguments = (type as ParameterizedType).actualTypeArguments
                    //获取第一个就是泛型T的Class
                    val clazz = actualTypeArguments[0] as Class<T>
                    println("getTableName: clazz " + clazz.name)
                    return clazz.simpleName
                }
            }
            return ""
        }
}