package com.example.baselibrary.http.util;

import android.util.Log;


import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


public class FileDownloadResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private BufferedSource bufferedSource;
    private int progress;
    private long contentLength;

    public FileDownloadResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        contentLength = responseBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long bytes = 0;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytes += bytesRead == -1 ? 0 : bytesRead;
                progress = (int) ((bytes * 100) / contentLength);
                Log.e("Update", "下载发送 progress：    " + progress);
                //RxBus.get().post(UPDATE, new FileDownLoadProgressEvent(progress));
                return bytesRead;
            }
        };
    }
}
