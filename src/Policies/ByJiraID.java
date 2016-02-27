package Policies;

/**
 * Default policy, if commit description contains JIRA id, create a link between JIRA and the commit
 * Output: json file with links
 * @author Haihong Luo
 */
public interface ByJiraID {
	public void setLinkByJiraID(String location) throws Exception;
}
