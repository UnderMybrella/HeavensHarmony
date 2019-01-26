/*
 *     This file is part of Discord4J.
 *
 *     Discord4J is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Discord4J is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.abimon.heavens_harmony

import java.util.*

class EmbedObject {
    /**
     * The title of the embed.
     */
    var title: String? = null
    /**
     * The type of the embed.
     */
    var type: String = "rich"
    /**
     * The description of the embed.
     */
    var description: String? = null
    /**
     * The URL of the embed.
     */
    var url: String? = null
    /**
     * The timestamp of the embed.
     */
    var timestamp: String? = null
    /**
     * The side color of the embed.
     */
    var color: Int = 0
    /**
     * The footer of the embed.
     */
    var footer: FooterObject? = null
    /**
     * The image of the embed.
     */
    var image: ImageObject? = null
    /**
     * The thumbnail of the embed.
     */
    var thumbnail: ThumbnailObject? = null
    /**
     * The video of the embed.
     */
    var video: VideoObject? = null
    /**
     * The provider of the embed.
     */
    var provider: ProviderObject? = null
    /**
     * The author of the embed.
     */
    var author: AuthorObject? = null
    /**
     * The fields of the embed.
     */
    var fields: Array<EmbedFieldObject>? = null

    constructor() {}

    constructor(title: String?, type: String, description: String?, url: String?, timestamp: String?, color: Int, footer: FooterObject?, image: ImageObject?, thumbnail: ThumbnailObject?, video: VideoObject?, provider: ProviderObject?, author: AuthorObject?, fields: Array<EmbedFieldObject>?) {
        this.title = title
        this.type = type
        this.description = description
        this.url = url
        this.timestamp = timestamp
        this.color = color
        this.footer = footer
        this.image = image
        this.thumbnail = thumbnail
        this.video = video
        this.provider = provider
        this.author = author
        this.fields = fields
    }

    /**
     * Represents a json thumbnail object.
     */
    class ThumbnailObject {
        /**
         * The URL of the thumbnail.
         */
        var url: String? = null
        /**
         * The proxied URL of the thumbnail.
         */
        var proxy_url: String? = null
        /**
         * The height of the thumbnail.
         */
        var height: Int = 0
        /**
         * The width of the thumbnail.
         */
        var width: Int = 0

        constructor() {}

        constructor(url: String?, proxy_url: String?, height: Int, width: Int) {
            this.url = url
            this.proxy_url = proxy_url
            this.height = height
            this.width = width
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ThumbnailObject) return false

            if (url != other.url) return false
            if (proxy_url != other.proxy_url) return false
            if (height != other.height) return false
            if (width != other.width) return false

            return true
        }

        override fun hashCode(): Int {
            var result = url?.hashCode() ?: 0
            result = 31 * result + (proxy_url?.hashCode() ?: 0)
            result = 31 * result + height
            result = 31 * result + width
            return result
        }

        operator fun component1(): String? = url
        operator fun component2(): String? = proxy_url
        operator fun component3(): Int = height
        operator fun component4(): Int = width
    }

    /**
     * Represents a json video object.
     */
    class VideoObject {
        /**
         * The URL of the video.
         */
        var url: String? = null
        /**
         * The height of the video.
         */
        var height: Int = 0
        /**
         * The width of the video.
         */
        var width: Int = 0

        constructor() {}

        constructor(url: String?, height: Int, width: Int) {
            this.url = url

            this.height = height
            this.width = width
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is VideoObject) return false

            if (url != other.url) return false
            if (height != other.height) return false
            if (width != other.width) return false

            return true
        }

        override fun hashCode(): Int {
            var result = url?.hashCode() ?: 0
            result = 31 * result + height
            result = 31 * result + width
            return result
        }

        operator fun component1(): String? = url
        operator fun component2(): Int = height
        operator fun component3(): Int = width
    }

    /**
     * Represents a json image object.
     */
    class ImageObject {
        /**
         * The URL of the image.
         */
        var url: String? = null
        /**
         * The proxied URL of the image.
         */
        var proxy_url: String? = null
        /**
         * The height of the image.
         */
        var height: Int = 0
        /**
         * The width of the image.
         */
        var width: Int = 0

        constructor() {}

        constructor(url: String?, proxy_url: String?, height: Int, width: Int) {
            this.url = url
            this.proxy_url = proxy_url
            this.height = height
            this.width = width
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ImageObject) return false

            if (url != other.url) return false
            if (proxy_url != other.proxy_url) return false
            if (height != other.height) return false
            if (width != other.width) return false

            return true
        }

        override fun hashCode(): Int {
            var result = url?.hashCode() ?: 0
            result = 31 * result + (proxy_url?.hashCode() ?: 0)
            result = 31 * result + height
            result = 31 * result + width
            return result
        }

        operator fun component1(): String? = url
        operator fun component2(): String? = proxy_url
        operator fun component3(): Int = height
        operator fun component4(): Int = width
    }

    /**
     * Represents a json provider object.
     */
    class ProviderObject {
        /**
         * The name of the provider.
         */
        var name: String? = null
        /**
         * The URL of the provider.
         */
        var url: String? = null

        constructor() {}

        constructor(name: String?, url: String?) {
            this.name = name
            this.url = url
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ProviderObject) return false

            if (name != other.name) return false
            if (url != other.url) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + (url?.hashCode() ?: 0)
            return result
        }

        operator fun component1(): String? = name
        operator fun component2(): String? = url
    }

    /**
     * Represents a json author object.
     */
    class AuthorObject {
        /**
         * The name of the author.
         */
        var name: String? = null
        /**
         * The URL of the author.
         */
        var url: String? = null
        /**
         * The URL of the author icon.
         */
        var icon_url: String? = null
        /**
         * The proxied URL of the author icon.
         */
        var proxy_icon_url: String? = null

        constructor() {}

        constructor(name: String?, url: String?, icon_url: String?, proxy_icon_url: String?) {
            this.name = name
            this.url = url
            this.icon_url = icon_url
            this.proxy_icon_url = proxy_icon_url
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AuthorObject) return false

            if (name != other.name) return false
            if (url != other.url) return false
            if (icon_url != other.icon_url) return false
            if (proxy_icon_url != other.proxy_icon_url) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + (url?.hashCode() ?: 0)
            result = 31 * result + (icon_url?.hashCode() ?: 0)
            result = 31 * result + (proxy_icon_url?.hashCode() ?: 0)
            return result
        }

        operator fun component1(): String? = name
        operator fun component2(): String? = url
        operator fun component3(): String? = icon_url
        operator fun component4(): String? = proxy_icon_url
    }

    /**
     * Represents a json footer object.
     */
    class FooterObject {
        /**
         * The text in the footer.
         */
        var text: String? = null
        /**
         * The URL of the icon in the footer.
         */
        var icon_url: String? = null
        /**
         * The proxied URL of the icon in the footer.
         */
        var proxy_icon_url: String? = null

        constructor() {}

        constructor(text: String?, icon_url: String?, proxy_icon_url: String?) {
            this.text = text
            this.icon_url = icon_url
            this.proxy_icon_url = proxy_icon_url
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FooterObject) return false

            if (text != other.text) return false
            if (icon_url != other.icon_url) return false
            if (proxy_icon_url != other.proxy_icon_url) return false

            return true
        }

        override fun hashCode(): Int {
            var result = text?.hashCode() ?: 0
            result = 31 * result + (icon_url?.hashCode() ?: 0)
            result = 31 * result + (proxy_icon_url?.hashCode() ?: 0)
            return result
        }

        operator fun component1(): String? = text
        operator fun component2(): String? = icon_url
        operator fun component3(): String? = proxy_icon_url
    }

    /**
     * Represents a json field object.
     */
    class EmbedFieldObject {
        /**
         * The name of the field.
         */
        var name: String? = null
        /**
         * The content in the field.
         */
        var value: String? = null
        /**
         * Whether the field should be displayed inline.
         */
        var inline: Boolean = false

        constructor() {}

        constructor(name: String?, value: String?, inline: Boolean) {
            this.name = name
            this.value = value
            this.inline = inline
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EmbedFieldObject) return false

            if (name != other.name) return false
            if (value != other.value) return false
            if (inline != other.inline) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + (value?.hashCode() ?: 0)
            result = 31 * result + inline.hashCode()
            return result
        }

        operator fun component1(): String? = name
        operator fun component2(): String? = value
        operator fun component3(): Boolean = inline
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmbedObject) return false

        if (title != other.title) return false
        if (type != other.type) return false
        if (description != other.description) return false
        if (url != other.url) return false
        if (timestamp != other.timestamp) return false
        if (color != other.color) return false
        if (footer != other.footer) return false
        if (image != other.image) return false
        if (thumbnail != other.thumbnail) return false
        if (video != other.video) return false
        if (provider != other.provider) return false
        if (author != other.author) return false
        if (!Arrays.equals(fields, other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        result = 31 * result + color
        result = 31 * result + (footer?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (thumbnail?.hashCode() ?: 0)
        result = 31 * result + (video?.hashCode() ?: 0)
        result = 31 * result + (provider?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (fields?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}