package Policies;

/**
 * By Date policy, if the updated date in JIRA = commit date, create a link between JIRA and the commit
 * Output: json file with links
 * @author Haihong Luo
 */
public interface ByDate {
	public void setLinkByDate(String location) throws Exception;
}
