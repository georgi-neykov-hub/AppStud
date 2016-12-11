package com.neykov.appstud.networking.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Georgi on 12/10/2016.
 */
public enum Status {
    @Expose
    @SerializedName("OK")
    OK,

    @Expose
    @SerializedName("ZERO_RESULTS ")
    NO_RESULTS,

    @Expose
    @SerializedName("OVER_QUERY_LIMIT ")
    ERROR_OVER_QUOTA,

    @Expose
    @SerializedName("REQUEST_DENIED  ")
    ERROR_REQUEST_DENIED,

    @Expose
    @SerializedName("INVALID_REQUEST  ")
    ERROR_INVALID_REQUEST,
}
