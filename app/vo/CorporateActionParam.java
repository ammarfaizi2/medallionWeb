package vo;

public class CorporateActionParam {
	private String keyId;
    String taskId;
    String announcementKey;
    String typeTransaction;
    
    public String getKeyId() {return keyId;}
    public void setKeyId(String keyId) {this.keyId = keyId;}
    public String getTaskId() {return taskId;}
    public void setTaskId(String taskId) {this.taskId = taskId;}
    public String getAnnouncementKey() {return announcementKey;}
    public void setAnnouncementKey(String announcementKey) {this.announcementKey = announcementKey;}
    public String getTypeTransaction() {return typeTransaction;}
    public void setTypeTransaction(String typeTransaction) {this.typeTransaction = typeTransaction;}
    @Override
    public String toString() {
            return "keyId="+keyId+", taskId="+taskId+", announcementKey="+announcementKey+", typeTransaction="+typeTransaction;
    }
}
