/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package dev.morling.jlink.plugins.sigcheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.mojo.animal_sniffer.SignatureBuilder;
import org.codehaus.mojo.animal_sniffer.logging.Logger;

final class StreamSignatureBuilder extends SignatureBuilder {
    StreamSignatureBuilder(OutputStream out, Logger logger) throws IOException {
        super(out, logger);
    }

    @Override
    public void process(String name, InputStream image) {
        try {
            super.process(name, image);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}