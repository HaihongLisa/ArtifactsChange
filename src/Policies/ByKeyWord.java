package Policies;

/**
 * By keyword policy, if the commit descriptions keyword can be found in the jira description
 * create a link between JIRA and the commit
 * Output: json file with links
 * @author Haihong Luo
 */
public interface ByKeyWord {
	public void setLinkByKeyWord(String location) throws Exception;
}
