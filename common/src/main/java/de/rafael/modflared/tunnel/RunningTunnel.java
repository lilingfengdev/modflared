package de.rafael.modflared.tunnel;

//------------------------------
//
// This class was developed by Rafael K.
// On 10/31/2022 at 11:29 PM
// In the project cloudflared
//
//------------------------------

import de.rafael.modflared.Modflared;
import de.rafael.modflared.binary.Cloudflared;
import de.rafael.modflared.tunnel.manager.TunnelManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public record RunningTunnel(Access access, Process process) {

    public static @NotNull CompletableFuture<RunningTunnel> createTunnel(@NotNull Cloudflared binary, @NotNull Access access) {
        var future = new CompletableFuture<RunningTunnel>();
        Modflared.EXECUTOR.execute(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(binary.buildCommand(access));
                // Since LINUX, MACOSX, and WINDOWS are the only options, this will work to only set the directory for Linux and MacOS
                if (Platform.get() != Platform.WINDOWS) {
                    processBuilder.directory(TunnelManager.DATA_FOLDER);
                }
                processBuilder.redirectErrorStream(true);
                var process = processBuilder.start();

                var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    TunnelManager.CLOUDFLARE_LOGGER.info(line);
                    if (line.contains("Start Websocket listener")) {
                        // Wait for the websocket to start (this is a hacky solution, but I don't really see a better way)
                        Thread.sleep(250);
                        future.complete(new RunningTunnel(access, process)); // Tunnel was started. Return running tunnel to minecraft client
                    }
                }
            } catch (IOException | InterruptedException exception) {
                Modflared.LOGGER.error("Failed to start cloudflared", exception);
                future.completeExceptionally(exception);
            }
        });
        return future;
    }

    public void closeTunnel() {
        process.destroy();
    }

    public record Access(String protocol, String hostname, InetSocketAddress tunnelAddress) {
        @Contract("_ -> new")
        public static @NotNull Access localWithRandomPort(String host) {
            return new Access("tcp", host, new InetSocketAddress("127.0.0.1", (int) (Math.random() * 10000 + 25565)));
        }

        public String @NotNull [] command(@NotNull String fileName, boolean prefix) {
            return new String[] {(prefix && Platform.get() != Platform.WINDOWS ? "./" : "") + fileName, "access", protocol, "--hostname", hostname, "--url", tunnelAddress.getHostString() + ":" + tunnelAddress.getPort()};
        }
    }

}
