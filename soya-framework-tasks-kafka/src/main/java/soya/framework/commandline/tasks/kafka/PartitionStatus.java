package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.common.PartitionInfo;

public class PartitionStatus extends PartitionInfo {

    private long begin;
    private long end;

    public PartitionStatus(PartitionInfo partitionInfo, long begin, long end) {
        super(partitionInfo.topic(), partitionInfo.partition(),
                partitionInfo.leader(), partitionInfo.replicas(), partitionInfo.inSyncReplicas(), partitionInfo.offlineReplicas());
        this.begin = begin;
        this.end = end;
    }
}
