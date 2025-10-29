# RSS Module

📡 A comprehensive RSS/Atom feed module for BoxLang that brings full-featured feed reading and creation capabilities to your applications!

This module provides powerful RSS and Atom feed capabilities to the [BoxLang](https://boxlang.io/) language, making it easy to read, parse, create, and manage syndication feeds with minimal code.

## ✨ Features

- 📖 **Read**: Parse RSS 2.0, RSS 1.0 (RDF), and Atom feeds from URLs or files
- ✍️ **Create**: Generate RSS 2.0 and Atom feeds with full metadata support
- 🎙️ **iTunes Podcast**: Auto-detect and parse iTunes podcast extensions (23 additional fields)
- 📹 **Media RSS**: Auto-detect and parse Media RSS extensions (thumbnails, content, player)
- 🔄 **Multiple Sources**: Read from multiple feed URLs simultaneously and merge results
- 🎯 **Filtering**: Apply custom filters to feed items during reading
- 📄 **Pagination**: Limit items with `maxItems` parameter
- 🔌 **Flexible Output**: Return results as structs, save to files, or get raw XML
- 🏢 **Enterprise Grade**: Built and Supported by Ortus Solutions
- 🔁 **CFML Compatible**: Drop-in replacement for CFML's `cffeed` tag

## 📦 Installation

### Install via CommandBox

If you are using CommandBox for your web applications, simply run:

```bash
box install bx-rss
```

The module will automatically register and be available as `bxrss` in your BoxLang applications.

## 🚀 Quick Start

### Using the BIF (Easiest Way)

The simplest way to read an RSS feed is with the `rss()` function:

```boxlang
feedData = rss( "https://example.com/feed.xml" );

println( "Found #feedData.items.size()# items" );
println( "Feed title: #feedData.channel.title#" );
```

### Using the Component

You can also use the `<bx:feed>` component for more control:

```boxlang
bx:feed
    action="read"
    source="https://example.com/feed.xml"
    result="feedData";

println( "Found #feedData.items.size()# items" );
println( "Feed title: #feedData.channel.title#" );
```

That's it! 🎉 You now have feed data parsed and ready to use.

💡 **Pro Tip**: The `rss()` BIF is perfect for quick feed reading, while the component gives you more options like multiple output variables, file writing, and creating feeds.

## 🔧 Feed Actions

The module supports two core operations:

### 📖 Read

Parse existing RSS/Atom feeds from URLs or files.

- **Use Case**: Display blog posts, news articles, podcast episodes
- **Returns**: Struct with `items` array and `channel` metadata
- **Features**: Auto-detection of extensions, filtering, pagination, multiple output options
- **Extension Support**: Automatically includes iTunes podcast and Media RSS fields when present

### ✍️ Create

Generate new RSS/Atom feeds from your data.

- **Use Case**: Expose your content as RSS/Atom feeds, create podcasts
- **Returns**: XML string, file output, or both
- **Features**: Full metadata control, multiple item formats, character escaping
- **Formats**: RSS 2.0 or Atom

## 📚 Component Reference

### 🔐 `<bx:feed>` Component

The main component for all RSS/Atom feed operations.

#### Core Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `action` | string | No | `"read"` | Action to perform: `"read"` or `"create"` |

#### Read Action Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `source` | string | Yes | - | URL or file path to the feed |
| `result` | string | No | - | Variable name to store full feed structure (items + channel) |
| `name` | string | No | - | Alias for `result` (backward compatibility) |
| `properties` | string | No | - | Variable name to store channel metadata only |
| `query` | string | No | - | Variable name to store items array only |
| `xmlVar` | string | No | - | Variable name to store raw XML string |
| `outputFile` | string | No | - | File path to write the feed XML |
| `overwrite` | boolean | No | `false` | Whether to overwrite existing output file |
| `timeout` | numeric | No | `60` | HTTP timeout in seconds |
| `userAgent` | string | No | `"BoxLang-RSS-Module/1.0"` | Custom User-Agent for HTTP requests |
| `maxItems` | numeric | No | `0` | Maximum items to return (0 = no limit) |
| `itunes` | boolean | No | `false` | Force iTunes podcast reader |
| `mediaRss` | boolean | No | `false` | Force Media RSS reader |

#### Create Action Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `properties` | struct | Yes | - | Feed metadata (title, description, link, etc.) |
| `data` | array/query | Yes | - | Feed items/entries |
| `name` | struct | No | - | Alternative: full feed structure (properties + items) |
| `query` | any | No | - | Alias for `data` (backward compatibility) |
| `columnMap` | struct | No | - | Map query columns to feed fields |
| `xmlVar` | string | No | - | Variable name to store generated XML |
| `outputFile` | string | No | - | File path to write the feed XML |
| `overwrite` | boolean | No | `false` | Whether to overwrite existing output file |
| `escapeChars` | boolean | No | `false` | Escape special characters in content |

## 💡 Examples

### Basic Examples

#### 📖 Simple Feed Read

Read and display feed items:

```boxlang
bx:feed
    action="read"
    source="https://example.com/blog/feed.xml"
    result="feedData";

println( "Blog: #feedData.channel.title#" );
println( "Items: #feedData.items.size()#" );

feedData.items.each( function( item ) {
    println( "- #item.title#: #item.link#" );
} );
```

💡 **Use Case**: Display latest blog posts or news articles.

#### 🎙️ Read iTunes Podcast Feed

Automatically detects and includes iTunes podcast fields:

```boxlang
bx:feed
    action="read"
    source="https://feeds.example.com/podcast.xml"
    result="podcast";

println( "Podcast: #podcast.channel.title#" );
println( "Author: #podcast.channel.itunesAuthor#" );

podcast.items.each( function( episode ) {
    println( "Episode: #episode.itunesTitle#" );
    println( "Duration: #episode.itunesDuration#" );
    println( "Season #episode.itunesSeason# Episode #episode.itunesEpisode#" );
} );
```

💡 **Use Case**: Display podcast episodes with rich metadata.

#### 📹 Read Media RSS Feed

Automatically detects and includes Media RSS thumbnail/content fields:

```boxlang
bx:feed
    action="read"
    source="https://vimeo.com/channels/staffpicks/videos/rss"
    result="videos";

videos.items.each( function( video ) {
    println( "Video: #video.title#" );
    if( !isNull( video.mediaThumbnail ) ) {
        println( "Thumbnail: #video.mediaThumbnail.url#" );
        println( "Size: #video.mediaThumbnail.width#x#video.mediaThumbnail.height#" );
    }
} );
```

💡 **Use Case**: Display video feeds with thumbnails.

#### 🎯 Filtered Feed Reading

Apply custom filters to feed items:

```boxlang
bx:feed
    action="read"
    source="https://news.example.com/feed.xml"
    result="recentNews"
    maxItems="10";

println( "Latest 10 news items:" );
recentNews.items.each( function( item ) {
    println( "- #item.title# (#dateFormat( item.publishedDate )#)" );
} );
```

💡 **Use Case**: Display latest N items from a feed.

#### 📄 Multiple Output Options

Use different output variables simultaneously:

```boxlang
bx:feed
    action="read"
    source="https://example.com/feed.xml"
    result="fullFeed"
    properties="metadata"
    query="items"
    xmlVar="rawXml"
    outputFile="/tmp/cached-feed.xml"
    overwrite="true";

// fullFeed has both items and channel
println( "Full structure: #fullFeed.items.size()# items" );

// metadata has only channel info
println( "Feed title: #metadata.title#" );

// items has only the items array
println( "Just items: #items.size()# entries" );

// rawXml has the original XML
println( "XML length: #rawXml.len()# characters" );
```

💡 **Use Case**: Flexible data access for different use cases.

#### ✍️ Create RSS Feed

Generate an RSS 2.0 feed from your data:

```boxlang
feedProps = {
    "version": "rss_2.0",
    "title": "My Blog",
    "link": "https://myblog.com",
    "description": "Latest posts from my blog",
    "publishedDate": now()
};

feedItems = [
    {
        "title": "First Post",
        "link": "https://myblog.com/post-1",
        "description": "This is my first blog post",
        "publishedDate": now(),
        "author": "john@example.com"
    },
    {
        "title": "Second Post",
        "link": "https://myblog.com/post-2",
        "description": "Another great post",
        "publishedDate": dateAdd( "d", -1, now() ),
        "author": "john@example.com"
    }
];

bx:feed
    action="create"
    properties=feedProps
    data=feedItems
    xmlVar="feedXml"
    outputFile="/var/www/feeds/blog.xml"
    overwrite="true";

println( "Feed created with #feedItems.size()# items" );
```

💡 **Use Case**: Expose your content as an RSS feed.

### Advanced Examples

#### 🎙️ Create iTunes Podcast Feed

Generate a podcast feed with iTunes extensions:

```boxlang
podcastProps = {
    "version": "rss_2.0",
    "title": "My Podcast",
    "link": "https://mypodcast.com",
    "description": "Weekly tech discussions",
    "publishedDate": now(),
    "itunesAuthor": "John Doe",
    "itunesSubtitle": "Tech Talk",
    "itunesSummary": "In-depth discussions about technology",
    "itunesImage": "https://mypodcast.com/artwork.jpg",
    "itunesExplicit": "false",
    "itunesCategories": ["Technology", "Business"]
};

episodes = [
    {
        "title": "Episode 1: Getting Started",
        "link": "https://mypodcast.com/episode-1",
        "description": "Our first episode",
        "publishedDate": now(),
        "author": "john@example.com",
        "itunesTitle": "Getting Started with Tech",
        "itunesDuration": "00:45:30",
        "itunesEpisode": "1",
        "itunesSeason": "1",
        "itunesEpisodeType": "full",
        "enclosure": {
            "url": "https://mypodcast.com/episodes/episode-1.mp3",
            "type": "audio/mpeg",
            "length": "45000000"
        }
    }
];

bx:feed
    action="create"
    properties=podcastProps
    data=episodes
    outputFile="/var/www/feeds/podcast.xml"
    overwrite="true";
```

💡 **Use Case**: Create a podcast feed for Apple Podcasts, Spotify, etc.

#### 🔄 Read Multiple Feeds

Merge items from multiple feeds:

```boxlang
sources = [
    "https://blog1.example.com/feed.xml",
    "https://blog2.example.com/feed.xml",
    "https://blog3.example.com/feed.xml"
];

bx:feed
    action="read"
    source=sources
    result="aggregated"
    maxItems="20";

println( "Aggregated #aggregated.items.size()# items from #sources.size()# feeds" );

// Items are automatically sorted by date
aggregated.items.each( function( item ) {
    println( "[#item.feed#] #item.title#" );
} );
```

💡 **Use Case**: Create a feed aggregator or news reader.

#### 📊 Create Feed from Query

Generate feed from database query results:

```boxlang
// Get blog posts from database
posts = queryExecute(
    "SELECT title, url, content, published_date, author_email
     FROM blog_posts
     WHERE status = 'published'
     ORDER BY published_date DESC
     LIMIT 50",
    []
);

feedProps = {
    "version": "rss_2.0",
    "title": "Company Blog",
    "link": "https://company.com/blog",
    "description": "Latest news and updates"
};

// Map query columns to feed fields
columnMap = {
    "title": "title",
    "link": "url",
    "description": "content",
    "publishedDate": "published_date",
    "author": "author_email"
};

bx:feed
    action="create"
    properties=feedProps
    data=posts
    columnMap=columnMap
    outputFile="/var/www/public/feed.xml"
    overwrite="true";
```

💡 **Use Case**: Generate feeds from database content.

#### 🌐 Custom User Agent

Use custom User-Agent for HTTP requests:

```boxlang
bx:feed
    action="read"
    source="https://api.example.com/feed.xml"
    result="feedData"
    userAgent="MyApp/2.0 (+https://myapp.com/bot)"
    timeout="30";
```

💡 **Use Case**: Identify your application to feed providers.

## 🎯 Extension Auto-Detection

### How It Works

The RSS module automatically detects and includes extension fields (iTunes podcast, Media RSS) when they are present in a feed, without requiring you to explicitly enable them.

**Auto-Detection Process**:
1. When no `itunes` or `mediaRss` flags are specified, the module starts with the iTunes reader
2. It checks the first item and channel for iTunes-specific fields
3. If no iTunes fields are found, it switches to the Media RSS reader
4. Extension fields are only included in the output when actually present

**Explicit Override**:
```boxlang
// Force iTunes reader (even if feed has no iTunes fields)
bx:feed source="feed.xml" result="data" itunes="true";

// Force Media RSS reader (even if feed has iTunes fields)
bx:feed source="feed.xml" result="data" mediaRss="true";
```

### iTunes Podcast Fields

When iTunes podcast extensions are detected, these additional fields are available:

**Channel Level** (in `feedData.channel`):
- `itunesAuthor` - Podcast author
- `itunesSubtitle` - Podcast subtitle
- `itunesSummary` - Longer description
- `itunesImage` - Artwork URL
- `itunesExplicit` - Content rating (true/false)
- `itunesCategories` - Array of category strings
- `itunesOwnerName` - Owner name
- `itunesOwnerEmail` - Owner email

**Item Level** (in each `feedData.items[]`):
- `itunesTitle` - Episode title
- `itunesDuration` - Duration (HH:MM:SS format)
- `itunesEpisode` - Episode number
- `itunesSeason` - Season number
- `itunesEpisodeType` - Type (full, trailer, bonus)
- `itunesExplicit` - Episode content rating
- `itunesAuthor` - Episode author
- `itunesSummary` - Episode summary
- `itunesSubtitle` - Episode subtitle
- `itunesImage` - Episode artwork URL

### Media RSS Fields

When Media RSS extensions are detected, these additional fields are available:

**Item Level** (in each `feedData.items[]`):

- `mediaThumbnail` - Struct with:
  - `url` - Thumbnail image URL
  - `width` - Image width in pixels
  - `height` - Image height in pixels
  - `time` - Time offset (for video thumbnails)
- Additional Media RSS fields as available in the feed

## 📖 BIF Reference

### `rss()` Function

The module also provides a `rss()` Built-In Function (BIF) for quick feed reading:

```boxlang
// Simple usage
feedData = rss( "https://example.com/feed.xml" );

// With options
feedData = rss(
    urls = "https://example.com/feed.xml",
    maxItems = 10,
    timeout = 30
);

// Multiple feeds
feedData = rss(
    urls = [
        "https://blog1.com/feed.xml",
        "https://blog2.com/feed.xml"
    ]
);
```

**Parameters**:
- `urls` (string/array, required) - Feed URL(s) to read
- `filter` (function, optional) - Filter function for items
- `maxItems` (numeric, optional) - Maximum items to return (default: 0 = all)
- `itunes` (boolean, optional) - Force iTunes reader (default: false = auto-detect)
- `mediaRss` (boolean, optional) - Force Media RSS reader (default: false = auto-detect)
- `userAgent` (string, optional) - Custom User-Agent
- `timeout` (numeric, optional) - Timeout in seconds (default: 25)

**Returns**: Struct with `items` array and `channel` metadata

## 🎯 Best Practices

### Performance

- ✅ **Cache feed data** - Cache parsed feeds to reduce HTTP requests
- ✅ **Use maxItems** - Limit items when you don't need the full feed
- ✅ **Set reasonable timeouts** - Default 60s is generous, adjust as needed
- ✅ **Handle failures gracefully** - Feeds can be temporarily unavailable
- ✅ **Validate feed URLs** - Check URLs before attempting to parse

### Feed Creation

- ✅ **Include all required fields** - title, link, description for channel and items
- ✅ **Use absolute URLs** - Ensure all links are fully qualified URLs
- ✅ **Set proper dates** - Use DateTime objects or valid date strings
- ✅ **Validate XML** - Test generated feeds with validators
- ✅ **Use escapeChars** - Enable when content contains HTML/special characters
- ✅ **Provide author info** - Include author/creator information for items

### iTunes Podcasts

- ✅ **Square artwork** - iTunes requires 1400x1400 to 3000x3000 pixels
- ✅ **Set explicit flag** - Always specify explicit/clean content rating
- ✅ **Include categories** - Help users discover your podcast
- ✅ **Add episode metadata** - Season, episode numbers, type (full/trailer/bonus)
- ✅ **Enclosure required** - Each episode must have an audio enclosure

### Security

- ✅ **Validate sources** - Only read from trusted feed URLs
- ✅ **Sanitize output** - Escape feed content when displaying in HTML
- ✅ **Set timeouts** - Prevent long-running operations
- ✅ **Handle errors** - Catch and log parsing failures
- ✅ **Use HTTPS** - Prefer HTTPS URLs for feed sources

## ❓ Troubleshooting

### Feed Reading Issues

**Problem**: Feed fails to parse or returns no items.

**Solutions**:
- ✅ Verify the URL is accessible (try in browser)
- ✅ Check if URL requires authentication
- ✅ Increase timeout for slow-loading feeds
- ✅ Verify feed is valid RSS/Atom (use feed validator)
- ✅ Check for network/firewall issues
- ✅ Review BoxLang logs for parsing errors

### Extension Fields Not Appearing

**Problem**: iTunes or Media RSS fields are missing.

**Solutions**:
- ✅ Verify the feed actually contains extension fields (view XML)
- ✅ Check that extensions are in correct namespace
- ✅ Try forcing reader: `itunes="true"` or `mediaRss="true"`
- ✅ Inspect raw XML with `xmlVar` attribute
- ✅ Validate feed with podcast/media RSS validators

### Feed Creation Problems

**Problem**: Generated feed is invalid or won't display.

**Solutions**:
- ✅ Validate feed XML with online validator (W3C, Podbase)
- ✅ Ensure all required fields are present (title, link, description)
- ✅ Use absolute URLs, not relative paths
- ✅ Check date formats are valid DateTime objects
- ✅ Enable `escapeChars="true"` if content has HTML
- ✅ Verify enclosure URLs are accessible (for podcasts)

### File Output Issues

**Problem**: Feed won't save to file.

**Solutions**:
- ✅ Verify output directory exists and is writable
- ✅ Check file permissions on the target path
- ✅ Enable `overwrite="true"` to replace existing files
- ✅ Use absolute file paths, not relative
- ✅ Ensure sufficient disk space

## 🔗 Resources

- [RSS 2.0 Specification](https://www.rssboard.org/rss-specification)
- [Atom 1.0 Specification](https://datatracker.ietf.org/doc/html/rfc4287)
- [iTunes Podcast RSS Tags](https://help.apple.com/itc/podcasts_connect/#/itcb54353390)
- [Media RSS Specification](http://www.rssboard.org/media-rss)
- [Feed Validator](https://validator.w3.org/feed/)
- [Podcast Validator](https://podba.se/validate/)

---

## THE DAILY BREAD

> "I am the way, and the truth, and the life; no one comes to the Father, but by me (JESUS)" Jn 14:1-12

---

<blockquote>
	Copyright Since 2023 by Ortus Solutions, Corp
	<br>
	<a href="https://www.boxlang.io">www.boxlang.io</a> |
	<a href="https://www.ortussolutions.com">www.ortussolutions.com</a>
</blockquote>