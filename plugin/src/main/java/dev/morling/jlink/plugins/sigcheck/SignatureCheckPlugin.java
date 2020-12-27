/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package dev.morling.jlink.plugins.sigcheck;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.codehaus.mojo.animal_sniffer.SignatureBuilder;
import org.codehaus.mojo.animal_sniffer.SignatureChecker;
import org.codehaus.mojo.animal_sniffer.logging.PrintWriterLogger;

import jdk.tools.jlink.plugin.Plugin;
import jdk.tools.jlink.plugin.PluginException;
import jdk.tools.jlink.plugin.ResourcePool;
import jdk.tools.jlink.plugin.ResourcePoolBuilder;
import jdk.tools.jlink.plugin.ResourcePoolEntry;

/**
 * A plug-in for jlink which adds a Jandex index to the image.
 *
 * @author Gunnar Morling
 *
 */
public class SignatureCheckPlugin implements Plugin {

    private static final String NAME = "check-signatures";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Category getType() {
        return Category.VERIFIER;
    }

    @Override
    public boolean hasArguments() {
        return false;
    }

    @Override
    public void configure(Map<String, String> config) {
    }

    @Override
    public String getDescription() {
        return "Adds an annotation index for one or more modules." + System.lineSeparator() +
                "<target-module>: name of the module which will host the index" + System.lineSeparator() +
                "<source-module-list>: comma-separated list of modules to include within the index";
    }

    @Override
    public ResourcePool transform(ResourcePool in, ResourcePoolBuilder out) {
        boolean broken = false;

        try {
            ByteArrayOutputStream signatureStream = new ByteArrayOutputStream();

            var builder = new SignatureBuilder(signatureStream, new PrintWriterLogger( System.out ) ) {
                @Override
                public void process(String name, InputStream image) throws IOException {
                    super.process(name, image);
                }
            };

            in.entries()
                .filter(e -> isIncluded(e))
                .forEach(e -> {
                    try {
                        builder.process(e.path(), e.content());
                    }
                    catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                });

            builder.close();

            var sc = new SignatureChecker(
                    new ByteArrayInputStream(signatureStream.toByteArray()),
                    Collections.<String>emptySet(),
                    new PrintWriterLogger(System.out)) {

                @Override
                protected void process(String name, InputStream image) throws IOException {
                    super.process(name, image);
                }
            };

            sc.setSourcePath(Collections.<File>emptyList());

            in.entries()
                .filter(e -> isIncluded(e) && !e.path().startsWith("/java.base"))
                .forEach(e -> {
                    try {
                        sc.process(e.path(), e.content());
                    }
                    catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                });

            broken = sc.isSignatureBroken();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        if (broken) {
            throw new PluginException("Signature violations, check the logs");
        }

        in.transformAndCopy(
            e -> e,
            out
        );

        return out.build();
    }

    private boolean isIncluded(ResourcePoolEntry e) {
        return e.path().endsWith("class") && !e.path().endsWith("module-info.class");
    }
}
