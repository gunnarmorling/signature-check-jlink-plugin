/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package dev.morling.jlink.plugins.sigcheck;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.codehaus.mojo.animal_sniffer.logging.PrintWriterLogger;

import jdk.tools.jlink.plugin.Plugin;
import jdk.tools.jlink.plugin.PluginException;
import jdk.tools.jlink.plugin.ResourcePool;
import jdk.tools.jlink.plugin.ResourcePoolBuilder;
import jdk.tools.jlink.plugin.ResourcePoolEntry;

/**
 * A plug-in for jlink for checking the API references amongst the modules of an
 * application for consistency".
 *
 * @author Gunnar Morling
 *
 */
public class SignatureCheckPlugin implements Plugin {

    @Override
    public String getName() {
        return "check-signatures";
    }

    @Override
    public Category getType() {
        return Category.VERIFIER;
    }

    @Override
    public String getDescription() {
        return "Checks the API references amongst the modules of an application for consistency";
    }

    @Override
    public ResourcePool transform(ResourcePool in, ResourcePoolBuilder out) {
        try {
            byte[] signature = createSignature(in);
            boolean broken = checkSignature(in, signature);

            if (broken) {
                throw new PluginException("There are API ignature inconsistencies, please check the logs");
            }
        }
        catch(PluginException e) {
            throw e;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        in.transformAndCopy(e -> e, out);

        return out.build();
    }

    private byte[] createSignature(ResourcePool in) throws IOException {
        ByteArrayOutputStream signatureStream = new ByteArrayOutputStream();

        var builder = new StreamSignatureBuilder(signatureStream, new PrintWriterLogger(System.out));

        in.entries()
            .filter(e -> isClassFile(e) && !isModuleInfo(e))
            .forEach(e -> builder.process(e.path(), e.content()));

        builder.close();

        return signatureStream.toByteArray();
    }

    private boolean checkSignature(ResourcePool in, byte[] signature) throws IOException {
        var checker = new StreamSignatureChecker(
                new ByteArrayInputStream(signature),
                Collections.<String>emptySet(),
                new PrintWriterLogger(System.out)
        );

        checker.setSourcePath(Collections.<File>emptyList());

        in.entries()
            .filter(e -> isClassFile(e) && !isModuleInfo(e) && !isJdkClass(e))
            .forEach(e -> checker.process(e.path(), e.content()));

        return checker.isSignatureBroken();
    }

    private boolean isJdkClass(ResourcePoolEntry e) {
        return e.path().startsWith("/java.") || e.path().startsWith("/javax.") || e.path().startsWith("/jdk.");
    }

    private boolean isModuleInfo(ResourcePoolEntry e) {
        return e.path().endsWith("module-info.class");
    }

    private boolean isClassFile(ResourcePoolEntry e) {
        return e.path().endsWith("class");
    }
}
