package com.polaris.lesscode.form.model;

import lombok.Data;

/**
 * 存储信息
 *
 */
@Data
public class Storage {

	private String database;
	
	private String table;

	public Storage(String database, String table) {
		this.database = database;
		this.table = table;
	}

	public Storage() {
		super();
	}
	
}
