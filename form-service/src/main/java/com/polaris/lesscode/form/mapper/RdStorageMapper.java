package com.polaris.lesscode.form.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.polaris.lesscode.form.model.StorageValue;
import com.polaris.lesscode.form.model.VirtualColumn;

public interface RdStorageMapper {

	public int createTable(@Param("database") String database, @Param("table") String table);
	
	public int alterColumn(@Param("database") String database, @Param("table") String table, @Param("adds") List<VirtualColumn> adds, @Param("dels") List<VirtualColumn> dels);
	
	public int insert(@Param("database") String database, @Param("table") String table, @Param("value") StorageValue value);
	
	public int insertBatch(@Param("database") String database, @Param("table") String table, @Param("values") List<StorageValue> values);
	
	public int update(@Param("database") String database, @Param("table") String table, @Param("value") StorageValue value);
	
}
