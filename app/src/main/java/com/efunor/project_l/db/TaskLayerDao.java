package com.efunor.project_l.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TASK_LAYER".
*/
public class TaskLayerDao extends AbstractDao<TaskLayer, Long> {

    public static final String TABLENAME = "TASK_LAYER";

    /**
     * Properties of entity TaskLayer.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Self_id = new Property(1, String.class, "self_id", false, "SELF_ID");
        public final static Property Parent_id = new Property(2, String.class, "parent_id", false, "PARENT_ID");
        public final static Property Update_time = new Property(3, Long.class, "update_time", false, "UPDATE_TIME");
        public final static Property Tid = new Property(4, String.class, "tid", false, "TID");
        public final static Property Is_indent = new Property(5, Boolean.class, "is_indent", false, "IS_INDENT");
        public final static Property Order_list = new Property(6, Integer.class, "order_list", false, "ORDER_LIST");
    };


    public TaskLayerDao(DaoConfig config) {
        super(config);
    }
    
    public TaskLayerDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TASK_LAYER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SELF_ID\" TEXT," + // 1: self_id
                "\"PARENT_ID\" TEXT," + // 2: parent_id
                "\"UPDATE_TIME\" INTEGER," + // 3: update_time
                "\"TID\" TEXT," + // 4: tid
                "\"IS_INDENT\" INTEGER," + // 5: is_indent
                "\"ORDER_LIST\" INTEGER);"); // 6: order_list
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TASK_LAYER\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TaskLayer entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String self_id = entity.getSelf_id();
        if (self_id != null) {
            stmt.bindString(2, self_id);
        }
 
        String parent_id = entity.getParent_id();
        if (parent_id != null) {
            stmt.bindString(3, parent_id);
        }
 
        Long update_time = entity.getUpdate_time();
        if (update_time != null) {
            stmt.bindLong(4, update_time);
        }
 
        String tid = entity.getTid();
        if (tid != null) {
            stmt.bindString(5, tid);
        }
 
        Boolean is_indent = entity.getIs_indent();
        if (is_indent != null) {
            stmt.bindLong(6, is_indent ? 1L: 0L);
        }
 
        Integer order_list = entity.getOrder_list();
        if (order_list != null) {
            stmt.bindLong(7, order_list);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TaskLayer readEntity(Cursor cursor, int offset) {
        TaskLayer entity = new TaskLayer( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // self_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // parent_id
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // update_time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // tid
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // is_indent
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // order_list
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TaskLayer entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSelf_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setParent_id(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUpdate_time(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setTid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIs_indent(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setOrder_list(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TaskLayer entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TaskLayer entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}