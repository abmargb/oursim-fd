package br.edu.ufcg.lsd.oursim.util;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
public class GWAJobDescription {
	
	public long JobID;
	public long SubmitTime;
	public long WaitTime;
	public long RunTime;
	public long NProc;
	public long AverageCPUTimeUsed;
	public String UsedMemory;
	public long ReqNProcs;
	public long ReqTime;
	public long ReqMemory;
	public long Status;
	public String UserID;
	public String GroupID;
	public long ExecutableID;
	public String QueueID;
	public long PartitionID;
	public String OrigSiteID;
	public String LastRunSiteID;
	public long UNKNOW;
	public long JobStructure;
	public long JobStructureParams;
	public long UsedNetwork;
	public long UsedLocalDiskSpace;
	public long UsedResources;
	public long ReqPlatform;
	public long ReqNetwork;
	public long RequestedLocalDiskSpace;
	public long RequestedResources;
	public long VirtualOrganizationID;
	public long ProjectID;
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		
		.append("JobID", JobID)
		.append("SubmitTime", SubmitTime)
		.append("WaitTime", WaitTime)
		.append("RunTime", RunTime)
		.append("NProc", NProc)
//		.append("ReqNProcs", ReqNProcs)
		.append("UserID", UserID)
		.append("GroupID", GroupID)
		.append("OrigSiteID", OrigSiteID)
//		.append("LastRunSiteID", LastRunSiteID)
//		.append("VirtualOrganizationID", VirtualOrganizationID)
		.append("ProjectID", ProjectID)
		
		.toString();
	}
	
	
	
}