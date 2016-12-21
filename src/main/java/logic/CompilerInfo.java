package logic;

import java.util.List;

public class CompilerInfo {
	private String packageName;
	private List<String> importNameList;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<String> getImportNameList() {
		return importNameList;
	}

	public void setImportNameList(List<String> importNameList) {
		this.importNameList = importNameList;
	}
}
