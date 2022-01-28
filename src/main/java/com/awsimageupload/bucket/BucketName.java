package com.awsimageupload.bucket;

public enum BucketName {

    /** Created bucket manually on Amazon Console
     * Could create buckets through CloudFormation or Terraform too,
     * but it's not advisable to use code to do so.
     */

    PROFILE_IMAGE("kimani-image-upload");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
