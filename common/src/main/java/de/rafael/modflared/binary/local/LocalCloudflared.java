package de.rafael.modflared.binary.local;

import de.rafael.modflared.Modflared;
import de.rafael.modflared.binary.Cloudflared;
import de.rafael.modflared.tunnel.RunningTunnel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class LocalCloudflared extends Cloudflared {

    public LocalCloudflared(String version) {
        super(version);
    }

    @Override
    public CompletableFuture<Void> prepare() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String[] buildCommand(RunningTunnel.@NotNull Access access) {
        return access.command("cloudflared", false);
    }

    public static @Nullable Cloudflared tryCreate() {
        // Check if cloudflared is already installed on the system
        try {
            var builder = new ProcessBuilder("cloudflared", "--version");
            var process = builder.start();
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String versionString = reader.readLine();
            String version = versionString.split(" ")[2];
            Modflared.LOGGER.info("Cloudflared output: {}", versionString);
            Modflared.LOGGER.info("Cloudflared version {} is already installed on the system", version);
            return new LocalCloudflared(version);
        } catch (Throwable ignored) {
            Modflared.LOGGER.info("Cloudflared is not installed on the system. Downloading it if necessary...");
        }
        return null;
    }

}
