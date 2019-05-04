package com.product.sampling.httpmoudle.body;

import com.product.sampling.httpmoudle.interfaces.IDownloadCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 *
 * 创建时间：2018/5/26
 * 描述：
 */
public class ResponseProgressBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private IDownloadCallback downloadCallback;
    private BufferedSource bufferedSource;

    public ResponseProgressBody(ResponseBody responseBody, IDownloadCallback downloadCallback) {
        this.mResponseBody = responseBody;
        this.downloadCallback = downloadCallback;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {

            long totalBytesRead;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += ((bytesRead != -1) ? bytesRead : 0);
                if (downloadCallback != null) {
                    downloadCallback.onProgress(totalBytesRead, mResponseBody.contentLength());
                }
                return bytesRead;
            }
        };
    }
}