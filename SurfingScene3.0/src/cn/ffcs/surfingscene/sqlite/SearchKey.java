package cn.ffcs.surfingscene.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_search_key")
public class SearchKey {
	@DatabaseField(generatedId = true)
	public Integer id;

	@DatabaseField(columnName = "key_word", index = true)
	public String keyWord; // 搜索关键词

	public static SearchKey convertEntity(String key) {
		SearchKey searchKey = new SearchKey();
		searchKey.keyWord = key;
		return searchKey;
	}
}
