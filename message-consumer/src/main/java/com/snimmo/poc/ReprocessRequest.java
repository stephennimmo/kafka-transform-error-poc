package com.snimmo.poc;

public class ReprocessRequest {

    private int partition;
    private int offset;

    public ReprocessRequest(int partition, int offset) {
        this.partition = partition;
        this.offset = offset;
    }

    public int getPartition() {
        return partition;
    }

    public int getOffset() {
        return offset;
    }
}
