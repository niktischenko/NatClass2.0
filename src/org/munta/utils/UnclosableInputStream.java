package org.munta.utils;

import java.io.IOException;
import java.io.InputStream;

public class UnclosableInputStream extends InputStream {

    private InputStream is;
    private Boolean closingAllowed;

    public UnclosableInputStream(InputStream is) {
        this.is = is;
        closingAllowed = false;
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public void close() throws IOException {
        if (closingAllowed) {
            super.close();
        }
    }

    public void forceClose() throws IOException {
        super.close();
    }

    public void allowClose() {
        closingAllowed = true;
    }
}
