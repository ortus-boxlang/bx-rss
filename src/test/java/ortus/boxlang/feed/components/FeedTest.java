package ortus.boxlang.feed.components;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.feed.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;

public class FeedTest extends BaseIntegrationTest {

	@Test
	@DisplayName( "Can read RSS feed using Feed component" )
	public void testReadRSSFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" result="feedData" maxItems="5";
		    count = feedData.items.size();
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 1 );

		IStruct feedData = variables.getAsStruct( Key.of( "feedData" ) );
		assertThat( feedData ).isNotNull();
		assertThat( feedData.containsKey( "items" ) ).isTrue();
		assertThat( feedData.containsKey( "channel" ) ).isTrue();
	}

	@Test
	@DisplayName( "Can read Atom feed using Feed component" )
	public void testReadAtomFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://feeds.feedburner.com/TheHackersNews" result="feedData" maxItems="3";
		    count = feedData.items.size();
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 1 );
	}

	@Test
	@DisplayName( "Can create RSS 2.0 feed with array data" )
	public void testCreateRSSFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Test Feed",
		        description: "A test RSS feed",
		        link: "https://example.com",
		        author: "Test Author",
		        language: "en-US"
		    };

		    data = [
		        {
		            title: "First Post",
		            link: "https://example.com/post1",
		            description: "This is the first post",
		            author: "John Doe",
		            publishedDate: now()
		        },
		        {
		            title: "Second Post",
		            link: "https://example.com/post2",
		            description: "This is the second post",
		            author: "Jane Smith",
		            publishedDate: now()
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" xmlVar="feedXml";

		    hasXml = len(feedXml) > 0;
		    containsRss = findNoCase("<rss", feedXml) > 0;
		    containsTitle = findNoCase("Test Feed", feedXml) > 0;
		    containsFirstPost = findNoCase("First Post", feedXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasXml" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsRss" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsFirstPost" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create Atom 1.0 feed" )
	public void testCreateAtomFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Test Atom Feed",
		        description: "A test Atom feed",
		        link: "https://example.com/atom"
		    };

		    data = [
		        {
		            title: "Atom Entry 1",
		            link: "https://example.com/entry1",
		            content: "Content of first entry"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" feedType="atom_1.0" xmlVar="atomXml";

		    hasXml = len(atomXml) > 0;
		    containsAtom = findNoCase("<feed", atomXml) > 0;
		    containsTitle = findNoCase("Test Atom Feed", atomXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasXml" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsAtom" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsTitle" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can write feed to output file" )
	public void testWriteFeedToFile() throws IOException {
		File tempFile = File.createTempFile( "test-feed-", ".xml" );
		tempFile.deleteOnExit();
		String filePath = tempFile.getAbsolutePath();

		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "File Test Feed",
		        description: "Testing file output",
		        link: "https://example.com"
		    };

		    data = [
		        {
		            title: "Test Item",
		            link: "https://example.com/item",
		            description: "Test description"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" outputFile="@filePath@" overwrite="true";

		    fileExists = fileExists("@filePath@");
		    """.replace( "@filePath@", filePath ),
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "fileExists" ) ) ).isTrue();

		// Verify file contents
		String content = Files.readString( tempFile.toPath() );
		assertThat( content ).contains( "File Test Feed" );
		assertThat( content ).contains( "Test Item" );
	}

	@Test
	@DisplayName( "Can use columnMap for query data" )
	public void testColumnMap() {
		// @formatter:off
		runtime.executeSource(
		    """
		    // Simulate query with different column names
		    data = [
		        {
		            headline: "News Item 1",
		            url: "https://example.com/news1",
		            body: "News body content",
		            writer: "Reporter Name"
		        },
		        {
		            headline: "News Item 2",
		            url: "https://example.com/news2",
		            body: "More news content",
		            writer: "Another Reporter"
		        }
		    ];

		    columnMap = {
		        title: "headline",
		        link: "url",
		        description: "body",
		        author: "writer"
		    };

		    properties = {
		        title: "News Feed",
		        description: "Latest news",
		        link: "https://example.com/news"
		    };

		    bx:feed action="create" properties="#properties#" data="#data#" columnMap="#columnMap#" xmlVar="newsXml";

		    containsHeadline = findNoCase("News Item 1", newsXml) > 0;
		    containsUrl = findNoCase("https://example.com/news1", newsXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "containsHeadline" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsUrl" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can escape special characters with escapeChars" )
	public void testEscapeChars() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Test & Special <Characters>",
		        description: "Testing escaping",
		        link: "https://example.com"
		    };

		    data = [
		        {
		            title: "Post with <tags> & ampersands",
		            link: "https://example.com/post",
		            description: "Content with 'quotes' and <html>"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" escapeChars="true" xmlVar="escapedXml";

		    // Escaped version should contain &lt; instead of <
		    containsEscaped = findNoCase("&lt;", escapedXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "containsEscaped" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Read action fails without source attribute" )
	public void testReadWithoutSource() {
		assertThrows( Exception.class, () -> {
			runtime.executeSource(
			    """
			    bx:feed action="read" result="feedData";
			    """,
			    context
			);
		} );
	}

	@Test
	@DisplayName( "Create action fails without properties attribute" )
	public void testCreateWithoutProperties() {
		assertThrows( Exception.class, () -> {
			runtime.executeSource(
			    """
			    data = [ { title: "Test" } ];
			    bx:feed action="create" data="#data#" xmlVar="xml";
			    """,
			    context
			);
		} );
	}

	@Test
	@DisplayName( "Create action fails without data attribute" )
	public void testCreateWithoutData() {
		assertThrows( Exception.class, () -> {
			runtime.executeSource(
			    """
			    properties = { title: "Test", link: "https://example.com" };
			    bx:feed action="create" properties="#properties#" xmlVar="xml";
			    """,
			    context
			);
		} );
	}

	@Test
	@DisplayName( "Supports backward compatibility with 'name' attribute" )
	public void testBackwardCompatibilityName() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" name="oldStyleResult" maxItems="2";
		    hasResult = isDefined("oldStyleResult");
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasResult" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create feed with categories" )
	public void testFeedWithCategories() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Category Test Feed",
		        description: "Testing categories",
		        link: "https://example.com"
		    };

		    data = [
		        {
		            title: "Categorized Post",
		            link: "https://example.com/cat-post",
		            description: "Post with categories",
		            category: "Tech,News,Development"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" xmlVar="catXml";

		    hasTechCategory = findNoCase("Tech", catXml) > 0;
		    hasNewsCategory = findNoCase("News", catXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasTechCategory" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasNewsCategory" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create feed using name structure input" )
	public void testCreateWithNameStructure() {
		// @formatter:off
		runtime.executeSource(
		    """
		    // First, read a feed to get the structure
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" result="originalFeed" maxItems="2";

		    // Modify the structure
		    originalFeed.title = "Modified Feed Title";
		    originalFeed.description = "Modified feed description";
		    originalFeed.link = "https://example.com/modified";

		    // Create a new feed using the modified structure with name attribute
		    bx:feed action="create" name="#originalFeed#" xmlVar="roundTripXml";

		    // Verify the XML contains modified values
		    hasModifiedTitle = findNoCase("Modified Feed Title", roundTripXml) > 0;
		    hasModifiedLink = findNoCase("https://example.com/modified", roundTripXml) > 0;
		    hasItems = findNoCase("<item>", roundTripXml) > 0 || findNoCase("<entry>", roundTripXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasModifiedTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasModifiedLink" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasItems" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create feed with custom structure using name attribute" )
	public void testCreateWithCustomNameStructure() {
		// @formatter:off
		runtime.executeSource(
		    """
		    // Create a complete feed structure manually
		    feedStructure = {
		        title: "Custom Feed via Name",
		        description: "Testing name attribute for create action",
		        link: "https://example.com/custom",
		        items: [
		            {
		                title: "First Custom Item",
		                link: "https://example.com/item1",
		                description: "Description of first item"
		            },
		            {
		                title: "Second Custom Item",
		                link: "https://example.com/item2",
		                description: "Description of second item",
		                author: "Test Author"
		            }
		        ]
		    };

		    // Create feed using name structure
		    bx:feed action="create" name="#feedStructure#" xmlVar="customXml";

		    // Verify the XML contains custom values
		    hasCustomTitle = findNoCase("Custom Feed via Name", customXml) > 0;
		    hasFirstItem = findNoCase("First Custom Item", customXml) > 0;
		    hasSecondItem = findNoCase("Second Custom Item", customXml) > 0;
		    hasAuthor = findNoCase("Test Author", customXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasCustomTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasFirstItem" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasSecondItem" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasAuthor" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create feed with publishedDate fields" )
	public void testCreateWithPublishedDates() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Date Test Feed",
		        description: "Testing date field handling",
		        link: "https://example.com",
		        publishedDate: parseDateTime("2024-01-15 10:30:00")
		    };

		    data = [
		        {
		            title: "Post with Date",
		            link: "https://example.com/dated-post",
		            description: "Post with published date",
		            publishedDate: parseDateTime("2024-01-20 14:15:00")
		        },
		        {
		            title: "Post with String Date",
		            link: "https://example.com/string-date",
		            description: "Post with string date that gets parsed",
		            publishedDate: "2024-02-01 09:00:00"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" xmlVar="dateXml";

		    hasXml = len(dateXml) > 0;
		    hasTitle = findNoCase("Date Test Feed", dateXml) > 0;
		    hasPost = findNoCase("Post with Date", dateXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasXml" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasPost" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can output to properties variable (metadata only)" )
	public void testReadWithPropertiesOutput() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" properties="metadata" maxItems="3";
		    
		    hasTitle = structKeyExists(metadata, "title");
		    hasDescription = structKeyExists(metadata, "description");
		    hasLink = structKeyExists(metadata, "link");
		    hasItems = structKeyExists(metadata, "items");
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasDescription" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasLink" ) ) ).isTrue();
		// metadata should NOT have items - only channel info
		assertThat( variables.getAsBoolean( Key.of( "hasItems" ) ) ).isFalse();
	}

	@Test
	@DisplayName( "Can output to query variable (items only)" )
	public void testReadWithQueryOutput() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" query="items" maxItems="5";
		    
		    isItemsArray = isArray(items);
		    itemCount = arrayLen(items);
		    hasFirstItem = itemCount > 0;
		    firstItemHasTitle = hasFirstItem && structKeyExists(items[1], "title");
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "isItemsArray" ) ) ).isTrue();
		assertThat( variables.getAsInteger( Key.of( "itemCount" ) ) ).isAtLeast( 1 );
		assertThat( variables.getAsBoolean( Key.of( "hasFirstItem" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "firstItemHasTitle" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can output to xmlVar (raw XML)" )
	public void testReadWithXmlVarOutput() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" xmlVar="rawXml" maxItems="3";
		    
		    hasXml = len(rawXml) > 0;
		    hasRssTag = findNoCase("<rss", rawXml) > 0 || findNoCase("<feed", rawXml) > 0;
		    hasChannelOrFeed = findNoCase("<channel", rawXml) > 0 || findNoCase("<feed", rawXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasXml" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasRssTag" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasChannelOrFeed" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can output to outputFile (save XML to disk)" )
	public void testReadWithOutputFile() throws IOException {
		File tempFile = File.createTempFile( "feed-test-", ".xml" );
		tempFile.deleteOnExit();
		String filePath = tempFile.getAbsolutePath();

		// @formatter:off
		runtime.executeSource(
		    String.format(
		        """
		        bx:feed action="read" source="https://www.engadget.com/rss.xml" outputFile="%s" overwrite="true" maxItems="3";
		        fileWasCreated = fileExists("%s");
		        """,
		        filePath.replace( "\\", "\\\\" ),
		        filePath.replace( "\\", "\\\\" )
		    ),
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "fileWasCreated" ) ) ).isTrue();
		
		// Verify file has content
		String content = Files.readString( tempFile.toPath() );
		assertThat( content ).isNotEmpty();
		assertThat( content ).containsMatch( "(?i)<rss|<feed" );
	}

	@Test
	@DisplayName( "Can use multiple output options simultaneously" )
	public void testReadWithMultipleOutputs() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed 
		        action="read" 
		        source="https://www.engadget.com/rss.xml" 
		        result="fullData"
		        properties="metadata"
		        query="items"
		        xmlVar="rawXml"
		        maxItems="3";
		    
		    hasFullData = structKeyExists(variables, "fullData");
		    hasMetadata = structKeyExists(variables, "metadata");
		    hasItems = structKeyExists(variables, "items");
		    hasRawXml = structKeyExists(variables, "rawXml");
		    
		    fullDataHasChannel = hasFullData && structKeyExists(fullData, "channel");
		    fullDataHasItems = hasFullData && structKeyExists(fullData, "items");
		    metadataHasTitle = hasMetadata && structKeyExists(metadata, "title");
		    itemsIsArray = hasItems && isArray(items);
		    xmlHasContent = hasRawXml && len(rawXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasFullData" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasMetadata" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasItems" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasRawXml" ) ) ).isTrue();
		
		assertThat( variables.getAsBoolean( Key.of( "fullDataHasChannel" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "fullDataHasItems" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "metadataHasTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "itemsIsArray" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "xmlHasContent" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can use name as alias for result output" )
	public void testReadWithNameAsAlias() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" name="feedData" maxItems="3";
		    
		    hasFeedData = structKeyExists(variables, "feedData");
		    hasChannel = hasFeedData && structKeyExists(feedData, "channel");
		    hasItems = hasFeedData && structKeyExists(feedData, "items");
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasFeedData" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasChannel" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasItems" ) ) ).isTrue();
	}

}
