package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.*;
import com.denizenscript.denizencore.tags.core.EscapeTagBase;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import discord4j.discordjson.json.*;

import java.util.ArrayList;
import java.util.List;

public class DiscordEmbedTag implements ObjectTag {

    public static DiscordEmbedTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("discordembed")
    public static DiscordEmbedTag valueOf(String string, TagContext context) {
        DiscordEmbedTag tag = new DiscordEmbedTag();
        //TODO
        return tag;
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("discordembed@")) {
            return true;
        }
        if (arg.contains("@")) {
            return false;
        }
        int comma = arg.indexOf(',');
        if (comma == -1) {
            return ArgumentHelper.matchesInteger(arg);
        }
        if (comma == arg.length() - 1) {
            return false;
        }
        return ArgumentHelper.matchesInteger(arg.substring(comma + 1));
    }

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

    public DiscordEmbedTag() {
        builder = ImmutableEmbedData.builder();
    }

    public DiscordEmbedTag(EmbedData data) {
        builder = ImmutableEmbedData.builder().from(data);
    }

    public static void registerTags() {
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            public void run(ReplaceableTagEvent event) {
                if (event.matches("discordembed") && !event.replaced()) {
                    event.setReplacedObject(CoreUtilities.autoAttrib(new DiscordEmbedTag(), event.getAttributes().fulfill(1)));
                }
            }
        }, "discordembed");
        registerTag("author_name", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isAuthorPresent() && !object.builder.build().author().get().name().isAbsent() ?
                        object.builder.build().author().get().name().get() : "null");
            }
            if(object.authorBuilder == null) {
                object.authorBuilder = EmbedAuthorData.builder();
            }
            object.authorBuilder.name(attribute.getContext(1));
            return object;
        });
        registerTag("author_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isAuthorPresent() && !object.builder.build().author().get().url().isAbsent() ?
                        object.builder.build().author().get().url().get() : "null");
            }
            if(object.authorBuilder == null) {
                object.authorBuilder = EmbedAuthorData.builder();
            }
            object.authorBuilder.url(attribute.getContext(1));
            return object;
        });
        registerTag("author_icon_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isAuthorPresent() && !object.builder.build().author().get().iconUrl().isAbsent() ?
                        object.builder.build().author().get().iconUrl().get() : "null");
            }
            if(object.authorBuilder == null) {
                object.authorBuilder = EmbedAuthorData.builder();
            }
            object.authorBuilder.iconUrl(attribute.getContext(1));
            return object;
        });
        registerTag("footer_text", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isFooterPresent() ?
                        object.builder.build().footer().get().text() : "null");
            }
            if(object.footerBuilder == null) {
                object.footerBuilder = EmbedFooterData.builder();
            }
            object.footerBuilder.text(attribute.getContext(1));
            return object;
        });
        registerTag("footer_icon_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isFooterPresent() && !object.builder.build().footer().get().iconUrl().isAbsent() ?
                        object.builder.build().footer().get().iconUrl().get() : "null");
            }
            if(object.footerBuilder == null) {
                object.footerBuilder = EmbedFooterData.builder();
            }
            object.footerBuilder.iconUrl(attribute.getContext(1));
            return object;
        });
        registerTag("image_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isImagePresent() && !object.builder.build().image().get().url().isAbsent() ?
                        object.builder.build().image().get().url().get() : "null");
            }
            if(object.imageBuilder == null) {
                object.imageBuilder = EmbedImageData.builder();
            }
            object.imageBuilder.url(attribute.getContext(1));
            return object;
        });
        registerTag("image_width", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isImagePresent() && !object.builder.build().image().get().width().isAbsent() ?
                        String.valueOf(object.builder.build().image().get().width().get()) : "null");
            }
            if(object.imageBuilder == null) {
                object.imageBuilder = EmbedImageData.builder();
            }
            object.imageBuilder.width(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("image_height", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isImagePresent() && !object.builder.build().image().get().height().isAbsent() ?
                        String.valueOf(object.builder.build().image().get().height().get()) : "null");
            }
            if(object.imageBuilder == null) {
                object.imageBuilder = EmbedImageData.builder();
            }
            object.imageBuilder.height(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("video_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isVideoPresent() && !object.builder.build().video().get().url().isAbsent() ?
                        object.builder.build().video().get().url().get() : "null");
            }
            if(object.videoBuilder == null) {
                object.videoBuilder = EmbedVideoData.builder();
            }
            object.videoBuilder.url(attribute.getContext(1));
            return object;
        });
        registerTag("video_width", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isVideoPresent() && !object.builder.build().video().get().width().isAbsent() ?
                        String.valueOf(object.builder.build().video().get().width().get()) : "null");
            }
            if(object.videoBuilder == null) {
                object.videoBuilder = EmbedVideoData.builder();
            }
            object.videoBuilder.width(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("video_height", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isVideoPresent() && !object.builder.build().video().get().height().isAbsent() ?
                        String.valueOf(object.builder.build().video().get().height().get()) : "null");
            }
            if(object.videoBuilder == null) {
                object.videoBuilder = EmbedVideoData.builder();
            }
            object.videoBuilder.height(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("thumbnail_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isThumbnailPresent() && !object.builder.build().thumbnail().get().url().isAbsent() ?
                        object.builder.build().thumbnail().get().url().get() : "null");
            }
            if(object.thumbnailBuilder == null) {
                object.thumbnailBuilder = EmbedThumbnailData.builder();
            }
            object.thumbnailBuilder.url(attribute.getContext(1));
            return object;
        });
        registerTag("thumbnail_width", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isThumbnailPresent() && !object.builder.build().thumbnail().get().width().isAbsent() ?
                        String.valueOf(object.builder.build().thumbnail().get().width().get()) : "null");
            }
            if(object.thumbnailBuilder == null) {
                object.thumbnailBuilder = EmbedThumbnailData.builder();
            }
            object.thumbnailBuilder.width(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("thumbnail_height", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isThumbnailPresent() && !object.builder.build().thumbnail().get().height().isAbsent() ?
                        String.valueOf(object.builder.build().thumbnail().get().height().get()) : "null");
            }
            if(object.thumbnailBuilder == null) {
                object.thumbnailBuilder = EmbedThumbnailData.builder();
            }
            object.thumbnailBuilder.height(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("provider_name", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isProviderPresent() && !object.builder.build().provider().get().name().isAbsent() ?
                        object.builder.build().provider().get().name().get() : "null");
            }
            if(object.providerBuilder == null) {
                object.providerBuilder = EmbedProviderData.builder();
            }
            object.providerBuilder.name(attribute.getContext(1));
            return object;
        });
        registerTag("provider_url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isProviderPresent() && !object.builder.build().provider().get().url().isAbsent() ?
                        object.builder.build().provider().get().url().get().get() : "null");
            }
            if(object.providerBuilder == null) {
                object.providerBuilder = EmbedProviderData.builder();
            }
            object.providerBuilder.url(attribute.getContext(1));
            return object;
        });
        registerTag("title", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isTitlePresent() ?
                        object.builder.build().title().get() : "null");
            }
            object.builder.title(attribute.getContext(1));
            return object;
        });
        registerTag("description", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isDescriptionPresent() ?
                        object.builder.build().description().get() : "null");
            }
            object.builder.description(attribute.getContext(1));
            return object;
        });
        registerTag("type", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isTypePresent() ?
                        object.builder.build().type().get() : "null");
            }
            object.builder.type(attribute.getContext(1));
            return object;
        });
        registerTag("color", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isColorPresent() ?
                        String.valueOf(object.builder.build().color().get()) : "null");
            }
            object.builder.color(Integer.valueOf(attribute.getContext(1)));
            return object;
        });
        registerTag("url", (attribute, object) -> {
            if(!attribute.hasContext(1)) {
                return new ElementTag(object.builder.build().isUrlPresent() ?
                        object.builder.build().url().get() : "null");
            }
            object.builder.url(attribute.getContext(1));
            return object;
        });
    }

    public static ObjectTagProcessor<DiscordEmbedTag> tagProcessor = new ObjectTagProcessor<>();

    public static void registerTag(String name, TagRunnable.ObjectInterface<DiscordEmbedTag> runnable, String... variants) {
        tagProcessor.registerTag(name, runnable, variants);
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    String prefix = "discordembed";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>'  ");
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String getObjectType() {
        return "DiscordEmbed";
    }

    @Override
    public String identify() {
        String id = "discordembed@";
        List<String> fields = new ArrayList<String>();
        if (builder.build().isAuthorPresent() && !builder.build().author().get().name().isAbsent()) { fields.add("author_name=" + escape(builder.build().author().get().name().get())); }
        if (builder.build().isAuthorPresent() && !builder.build().author().get().url().isAbsent()) { fields.add("author_url=" + escape(builder.build().author().get().url().get())); }
        if (builder.build().isAuthorPresent() && !builder.build().author().get().iconUrl().isAbsent()) { fields.add("author_icon_url=" + escape(builder.build().author().get().iconUrl().get())); }
        if (builder.build().isFooterPresent()) { fields.add("footer_text=" + escape(builder.build().footer().get().text())); }
        if (builder.build().isFooterPresent() && !builder.build().footer().get().iconUrl().isAbsent()) { fields.add("footer_icon_url=" + escape(builder.build().footer().get().iconUrl().get())); }
        if (builder.build().isImagePresent()) { fields.add("image_width=" + builder.build().image().get().width()); }
        if (builder.build().isImagePresent()) { fields.add("image_height=" + builder.build().image().get().height()); }
        if (builder.build().isImagePresent() && !builder.build().image().get().url().isAbsent()) { fields.add("image_url=" + escape(builder.build().image().get().url().get())); }
        if (builder.build().isVideoPresent()) { fields.add("video_width=" + builder.build().video().get().width()); }
        if (builder.build().isVideoPresent()) { fields.add("video_height=" + builder.build().video().get().height()); }
        if (builder.build().isVideoPresent() && !builder.build().video().get().url().isAbsent()) { fields.add("video_url=" + escape(builder.build().video().get().url().get())); }
        if (builder.build().isThumbnailPresent()) { fields.add("thumbnail_width=" + builder.build().thumbnail().get().width()); }
        if (builder.build().isThumbnailPresent()) { fields.add("thumbnail_height=" + builder.build().thumbnail().get().height()); }
        if (builder.build().isThumbnailPresent()) { fields.add("thumbnail_url=" + escape(builder.build().thumbnail().get().url().get())); }
        if (builder.build().isProviderPresent() && !builder.build().provider().get().name().isAbsent()) { fields.add("provider_name=" + escape(builder.build().provider().get().name().get())); }
        if (builder.build().isProviderPresent() && !builder.build().provider().get().url().isAbsent()) { fields.add("provider_url=" + escape(builder.build().provider().get().url().get().get())); }
        if (builder.build().isTitlePresent()) { fields.add("title=" + escape(builder.build().title().get())); }
        if (builder.build().isDescriptionPresent()) { fields.add("description=" + escape(builder.build().description().get())); }
        if (builder.build().isTypePresent()) { fields.add("type=" + escape(builder.build().type().get())); }
        if (builder.build().isColorPresent()) { fields.add("color=" + builder.build().color().get()); }
        if (builder.build().isUrlPresent()) { fields.add("url=" + escape(builder.build().url().get())); }
        id = id + String.join(";", fields);
        return id;
    }

    public static String escape(String input) {
        return CoreUtilities.replace(EscapeTagBase.escape(input), ",", "&com");
    }

    public static String unEscape(String input) {
        return CoreUtilities.replace(EscapeTagBase.unEscape(input), "&com", ",");
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
        return this;
    }
}

