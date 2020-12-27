/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
module dev.morling.jlink.plugins.sigcheck {
    exports dev.morling.jlink.plugins.sigcheck;
    requires jdk.jlink;
    requires animal.sniffer;
    requires org.objectweb.asm;
}
