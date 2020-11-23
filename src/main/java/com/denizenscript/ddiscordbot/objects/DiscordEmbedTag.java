package com.denizenscript.ddiscordbot.objects;

import com.denizenscript.ddiscordbot.scripts.DiscordEmbedScriptContainer;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.*;
import com.denizenscript.denizencore.tags.core.EscapeTagBase;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import discord4j.discordjson.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscordEmbedTag implements ObjectTag {

    public static DiscordEmbedTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("discordembed")
    public static DiscordEmbedTag valueOf(String string, TagContext context) {
        DiscordEmbedTag tag = new DiscordEmbedTag();
        string = string.replace("discordembed@", "");
        for(String arg : string.split(";")) {
            String key = arg.split("=")[0];
            String value = arg.split("=")[1];
            switch(key) {
                case "author_name":
                    tag.authorBuilder = (tag.authorBuilder != null ? tag.authorBuilder.name(value) : EmbedAuthorData.builder().name(value));
                    break;
                case "author_url":
                    tag.authorBuilder = (tag.authorBuilder != null ? tag.authorBuilder.url(value) : EmbedAuthorData.builder().url(value));
                    break;
                case "author_icon_url":
                    tag.authorBuilder = (tag.authorBuilder != null ? tag.authorBuilder.iconUrl(value) : EmbedAuthorData.builder().iconUrl(value));
                    break;
                case "footer_text":
                    tag.footerBuilder = (tag.footerBuilder != null ? tag.footerBuilder.text(value) : EmbedFooterData.builder().text(value));
                    break;
                case "footer_icon_url":
                    tag.footerBuilder = (tag.footerBuilder != null ? tag.footerBuilder.iconUrl(value) : EmbedFooterData.builder().iconUrl(value));
                    break;
                case "image_width":
                    tag.imageBuilder = (tag.imageBuilder != null ? tag.imageBuilder.width(Integer.valueOf(value)) : EmbedImageData.builder().width(Integer.valueOf(value)));
                    break;
                case "image_height":
                    tag.imageBuilder = (tag.imageBuilder != null ? tag.imageBuilder.height(Integer.valueOf(value)) : EmbedImageData.builder().height(Integer.valueOf(value)));
                    break;
                case "image_url":
                    tag.imageBuilder = (tag.imageBuilder != null ? tag.imageBuilder.url(value) : EmbedImageData.builder().url(value));
                    break;
                case "video_width":
                    tag.videoBuilder = (tag.videoBuilder != null ? tag.videoBuilder.width(Integer.valueOf(value)) : EmbedVideoData.builder().width(Integer.valueOf(value)));
                    break;
                case "video_height":
                    tag.videoBuilder = (tag.videoBuilder != null ? tag.videoBuilder.height(Integer.valueOf(value)) : EmbedVideoData.builder().height(Integer.valueOf(value)));
                    break;
                case "video_url":
                    tag.videoBuilder = (tag.videoBuilder != null ? tag.videoBuilder.url(value) : EmbedVideoData.builder().url(value));
                    break;
                case "thumbnail_width":
                    tag.thumbnailBuilder = (tag.thumbnailBuilder != null ? tag.thumbnailBuilder.width(Integer.valueOf(value)) : EmbedThumbnailData.builder().width(Integer.valueOf(value)));
                    break;
                case "thumbnail_height":
                    tag.thumbnailBuilder = (tag.thumbnailBuilder != null ? tag.thumbnailBuilder.height(Integer.valueOf(value)) : EmbedThumbnailData.builder().height(Integer.valueOf(value)));
                    break;
                case "thumbnail_url":
                    tag.thumbnailBuilder = (tag.thumbnailBuilder != null ? tag.thumbnailBuilder.url(value) : EmbedThumbnailData.builder().url(value));
                    break;
                case "provider_name":
                    tag.providerBuilder = (tag.providerBuilder != null ? tag.providerBuilder.name(value) : EmbedProviderData.builder().name(value));
                    break;
                case "provider_url":
                    tag.providerBuilder = (tag.providerBuilder != null ? tag.providerBuilder.url(value) : EmbedProviderData.builder().url(value));
                    break;
                case "title":
                    tag.builder = tag.builder.title(value);
                    break;
                case "description":
                    tag.builder = tag.builder.description(value);
                    break;
                case "embed_type":
                    tag.builder = tag.builder.type(value);
                    break;
                case "color":
                    tag.builder = tag.builder.color(Integer.valueOf(value));
                    break;
                case "url":
                    tag.builder = tag.builder.url(value);
                    break;
                case "fields":
                    if(true) {
                        MapTag map = MapTag.valueOf(unEscape(value), null);
                        List<EmbedFieldData> fields = new ArrayList<>();
                        for(Map.Entry<StringHolder, ObjectTag> e : map.map.entrySet()) {
                            tag.fields.add(EmbedFieldData.builder().name(e.getKey().str).value(((ElementTag)e.getValue()).asString()).inline(false).build());
                        }
                    }
                    break;
                case "inline_fields":
                    if(true) {
                        MapTag map = MapTag.valueOf(unEscape(value), null);
                        List<EmbedFieldData> fields = new ArrayList<>();
                        for(Map.Entry<StringHolder, ObjectTag> e : map.map.entrySet()) {
                            tag.fields.add(EmbedFieldData.builder().name(e.getKey().str).value(((ElementTag)e.getValue()).asString()).inline(true).build());
                        }
                    }
                    break;
            }
        }
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
    public List<EmbedFieldData> fields = new ArrayList<EmbedFieldData>();
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
        b = b.addAllFields(fields);
        return b;
    }

    public DiscordEmbedTag() { this.builder = ImmutableEmbedData.builder(); }

    public DiscordEmbedTag(ImmutableEmbedData.Builder builder) { this.builder = builder; }

    public DiscordEmbedTag(EmbedData data) {
        this.builder = ImmutableEmbedData.builder().from(data);
    }

    public static void registerTags() {
//        TagManager.registerTagHandler(new TagRunnable.RootForm() {
//            public void run(ReplaceableTagEvent event) {
//                if (event.matches("discordembed") && !event.replaced()) {
//                    if(event.getAttributes().hasContext(1)) {
//                        if(DiscordEmbedScriptContainer.containers.containsKey(event.getNameContext())) {
//                            event.setReplacedObject(CoreUtilities.autoAttrib(new DiscordEmbedTag(DiscordEmbedScriptContainer.containers.get(event.getNameContext()).build()), event.getAttributes().fulfill(1)));
//                        }
//                    } else {
//                        event.setReplacedObject(CoreUtilities.autoAttrib(new DiscordEmbedTag(), event.getAttributes().fulfill(1)));
//                    }
//                }
//            }
//        }, "discordembed");
        TagManager.registerTagHandler("discordembed", (attribute) -> {
            if (!attribute.hasContext(1)) {
                return CoreUtilities.autoAttrib(new DiscordEmbedTag(), attribute.fulfill(1));
            } else {
                if(DiscordEmbedScriptContainer.containers.containsKey(attribute.getContext(1))) {
                    return CoreUtilities.autoAttrib(new DiscordEmbedTag(DiscordEmbedScriptContainer.containers.get(attribute.getContext(1)).build()), attribute.fulfill(1));
                }
            }
            return null;
        });
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
        registerTag("embed_type", (attribute, object) -> {
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
        registerTag("fields", (attribute, object) -> {
            if(!attribute.hasContext(1) && object.fields != null) {
                MapTag map = new MapTag();
                for(EmbedFieldData s : object.fields) {
                    if(s.inline().get() && !s.name().isEmpty() && s.value() != null) {
                        map.putObject(s.name(), new ElementTag(s.value()));
                    }
                }
                return map;
            }
            MapTag map = MapTag.valueOf(attribute.getContext(1), null);
            for(Map.Entry<StringHolder, ObjectTag> e : map.map.entrySet()) {
                if (!e.getKey().toString().isEmpty() && !e.getValue().asType(ElementTag.class, null).asString().isEmpty()) {
                    object.fields.add(EmbedFieldData.builder().name(e.getKey().toString()).value(e.getValue().asType(ElementTag.class, null).asString()).inline(false).build());
                }
            }
            return object;
        });
        registerTag("inline_fields", (attribute, object) -> {
            if(!attribute.hasContext(1) && object.fields != null) {
                MapTag map = new MapTag();
                for(EmbedFieldData s : object.fields) {
                    if(s.inline().get() && !s.name().isEmpty() && s.value() != null) {
                        map.putObject(s.name(), new ElementTag(s.value()));
                    }
                }
                return map;
            }
            MapTag map = MapTag.valueOf(attribute.getContext(1), null);
            for(Map.Entry<StringHolder, ObjectTag> e : map.map.entrySet()) {
                if (!e.getKey().toString().isEmpty() && !e.getValue().asType(ElementTag.class, null).asString().isEmpty()) {
                    object.fields.add(EmbedFieldData.builder().name(e.getKey().toString()).value(e.getValue().asType(ElementTag.class, null).asString()).inline(true).build());
                }
            }
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
        ImmutableEmbedData build = build().build();
        if (build.isAuthorPresent() && !build.author().get().name().isAbsent()) { fields.add("author_name=" + escape(build.author().get().name().get())); }
        if (build.isAuthorPresent() && !build.author().get().url().isAbsent()) { fields.add("author_url=" + escape(build.author().get().url().get())); }
        if (build.isAuthorPresent() && !build.author().get().iconUrl().isAbsent()) { fields.add("author_icon_url=" + escape(build.author().get().iconUrl().get())); }
        if (build.isFooterPresent()) { fields.add("footer_text=" + escape(build.footer().get().text())); }
        if (build.isFooterPresent() && !build.footer().get().iconUrl().isAbsent()) { fields.add("footer_icon_url=" + escape(build.footer().get().iconUrl().get())); }
        if (build.isImagePresent()) { fields.add("image_width=" + build.image().get().width()); }
        if (build.isImagePresent()) { fields.add("image_height=" + build.image().get().height()); }
        if (build.isImagePresent() && !build.image().get().url().isAbsent()) { fields.add("image_url=" + escape(build.image().get().url().get())); }
        if (build.isVideoPresent()) { fields.add("video_width=" + build.video().get().width()); }
        if (build.isVideoPresent()) { fields.add("video_height=" + build.video().get().height()); }
        if (build.isVideoPresent() && !build.video().get().url().isAbsent()) { fields.add("video_url=" + escape(build.video().get().url().get())); }
        if (build.isThumbnailPresent()) { fields.add("thumbnail_width=" + build.thumbnail().get().width()); }
        if (build.isThumbnailPresent()) { fields.add("thumbnail_height=" + build.thumbnail().get().height()); }
        if (build.isThumbnailPresent()) { fields.add("thumbnail_url=" + escape(build.thumbnail().get().url().get())); }
        if (build.isProviderPresent() && !build.provider().get().name().isAbsent()) { fields.add("provider_name=" + escape(build.provider().get().name().get())); }
        if (build.isProviderPresent() && !build.provider().get().url().isAbsent()) { fields.add("provider_url=" + escape(build.provider().get().url().get().get())); }
        if (build.isTitlePresent()) { fields.add("title=" + escape(build.title().get())); }
        if (build.isDescriptionPresent()) { fields.add("description=" + escape(build.description().get())); }
        if (build.isTypePresent()) { fields.add("embed_type=" + escape(build.type().get())); }
        if (build.isColorPresent()) { fields.add("color=" + build.color().get()); }
        if (build.isUrlPresent()) { fields.add("url=" + escape(build.url().get())); }
        if (build.isFieldsPresent()) {
            MapTag map = new MapTag();
            MapTag inline = new MapTag();
            for(EmbedFieldData s : build.fields().get()) {
                if(s.inline().get()) {
                    inline.putObject(s.name(), new ElementTag(s.value()));
                } else {
                    map.putObject(s.name(), new ElementTag(s.value()));
                }
            }
            if(map.map.size() != 0) {
                fields.add("fields=" + escape(map.identify()));
            }
            if(inline.map.size() != 0) {
                fields.add("inline_fields=" + escape(inline.identify()));
            }
        }
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

