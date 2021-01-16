/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package dev.morling.jlink.plugins.sigcheck;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.codehaus.mojo.animal_sniffer.SignatureChecker;
import org.codehaus.mojo.animal_sniffer.logging.Logger;

class StreamSignatureChecker extends SignatureChecker {

    StreamSignatureChecker(InputStream in, Set<String> ignoredPackages, Logger logger) throws IOException {
        super(in, ignoredPackages, logger);
    }

    @Override
    protected void process(String name, InputStream image) {
        try {
            super.process(name, image);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
