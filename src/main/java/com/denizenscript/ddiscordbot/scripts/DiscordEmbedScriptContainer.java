package com.denizenscript.ddiscordbot.scripts;

import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import discord4j.discordjson.json.*;

import java.util.HashMap;
import java.util.Map;

public class DiscordEmbedScriptContainer extends ScriptContainer {

    public static Map<String, DiscordEmbedScriptContainer> containers = new HashMap<String, DiscordEmbedScriptContainer>();

    String name;
    public ImmutableEmbedData.Builder builder;
    public ImmutableEmbedAuthorData.Builder authorBuilder;
    public ImmutableEmbedFooterData.Builder footerBuilder;
    public ImmutableEmbedImageData.Builder imageBuilder;
    public ImmutableEmbedVideoData.Builder videoBuilder;
    public ImmutableEmbedThumbnailData.Builder thumbnailBuilder;
    public ImmutableEmbedProviderData.Builder providerBuilder;

    public ImmutableEmbedData.Builder build() {
        ImmutableEmbedData.Builder b = this.builder;
        if(authorBuilder != null) {
            b = b.author(authorBuilder.build());
        }
        if(footerBuilder != null) {
            b = b.footer(footerBuilder.build());
        }
        if(imageBuilder != null) {
            b = b.image(imageBuilder.build());
        }
        if(videoBuilder != null) {
            b = b.video(videoBuilder.build());
        }
        if(thumbnailBuilder != null) {
            b = b.thumbnail(thumbnailBuilder.build());
        }
        if(providerBuilder != null) {
            b = b.provider(providerBuilder.build());
        }
        return b;
    }

    public DiscordEmbedScriptContainer(YamlConfiguration config, String name) {
        super(config, name);
        this.name = name;
        this.builder = ImmutableEmbedData.builder();
        if (config.contains("author_name")) {
            if(authorBuilder == null) {
                authorBuilder = EmbedAuthorData.builder();
            }
            authorBuilder.name(config.getString("author_name"));
        }
        if (config.contains("author_url")) {
            if(authorBuilder == null) {
                authorBuilder = EmbedAuthorData.builder();
            }
            authorBuilder.url(config.getString("author_url"));
        }
        if (config.contains("author_icon_url")) {
            if(authorBuilder == null) {
                authorBuilder = EmbedAuthorData.builder();
            }
            authorBuilder.iconUrl(config.getString("author_icon_url"));
        }
        if (config.contains("footer_text")) {
            if(footerBuilder == null) {
                footerBuilder = EmbedFooterData.builder();
            }
            footerBuilder.text(config.getString("footer_text"));
        }
        if (config.contains("footer_icon_url")) {
            if(footerBuilder == null) {
                footerBuilder = EmbedFooterData.builder();
            }
            footerBuilder.text(config.getString("footer_icon_url"));
        }
        if (config.contains("image_url")) {
            if(imageBuilder == null) {
                imageBuilder = EmbedImageData.builder();
            }
            imageBuilder.url(config.getString("image_url"));
        }
        if (config.contains("image_width")) {
            if(imageBuilder == null) {
                imageBuilder = EmbedImageData.builder();
            }
            imageBuilder.width(Integer.valueOf(config.getString("image_width")));
        }
        if (config.contains("image_height")) {
            if(imageBuilder == null) {
                imageBuilder = EmbedImageData.builder();
            }
            imageBuilder.height(Integer.valueOf(config.getString("image_height")));
        }
        if (config.contains("video_url")) {
            if(videoBuilder == null) {
                videoBuilder = EmbedVideoData.builder();
            }
            videoBuilder.url(config.getString("video_url"));
        }
        if (config.contains("video_width")) {
            if(videoBuilder == null) {
                videoBuilder = EmbedVideoData.builder();
            }
            videoBuilder.height(Integer.valueOf(config.getString("video_width")));
        }
        if (config.contains("video_height")) {
            if(videoBuilder == null) {
                videoBuilder = EmbedVideoData.builder();
            }
            videoBuilder.height(Integer.valueOf(config.getString("video_height")));
        }
        if (config.contains("thumbnail_url")) {
            if(thumbnailBuilder == null) {
                thumbnailBuilder = EmbedThumbnailData.builder();
            }
            thumbnailBuilder.url(config.getString("thumbnail_url"));
        }
        if (config.contains("thumbnail_width")) {
            if(thumbnailBuilder == null) {
                thumbnailBuilder = EmbedThumbnailData.builder();
            }
            thumbnailBuilder.width(Integer.valueOf(config.getString("thumbnail_width")));
        }
        if (config.contains("thumbnail_height")) {
            if(thumbnailBuilder == null) {
                thumbnailBuilder = EmbedThumbnailData.builder();
            }
            thumbnailBuilder.height(Integer.valueOf(config.getString("thumbnail_height")));
        }
        if (config.contains("provider_name")) {
            if(providerBuilder == null) {
                providerBuilder = EmbedProviderData.builder();
            }
            providerBuilder.url(config.getString("provider_name"));
        }
        if (config.contains("provider_url")) {
            if(providerBuilder == null) {
                providerBuilder = EmbedProviderData.builder();
            }
            providerBuilder.url(config.getString("provider_url"));
        }
        if (config.contains("title")) {
            builder.title(config.getString("title"));
        }
        if (config.contains("description")) {
            builder.description(config.getString("description"));
        }
        if (config.contains("embed_type")) {
            builder.type(config.getString("embed_type"));
        }
        if (config.contains("color")) {
            builder.color(Integer.valueOf(config.getString("color")));
        }
        if (config.contains("url")) {
            builder.url(config.getString("url"));
        }
        containers.put(name, this);
    }
}