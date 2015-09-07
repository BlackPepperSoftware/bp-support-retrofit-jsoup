/*
 * Copyright 2014 Black Pepper Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.blackpepper.support.retrofit.jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import retrofit.converter.ConversionException;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypedElementTest {

	private static final String SHELL_HTML = "<html>\n <head></head> \n <body>  \n </body>\n</html>";
	
	private ExpectedException thrown = ExpectedException.none();
	
	@Rule
	public TestRule getThrown() {
		return thrown;
	}
	
	@Test
	public void constructorSetsElement() {
		Element element = mock(Element.class);
		
		TypedElement actual = new TypedElement(element);
		
		assertThat(actual.getElement(), is(element));
	}

	@Test
	public void constructorWithNullElementThrowsException() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("element");
		
		new TypedElement(null);
	}
	
	@Test
	public void mimeTypeReturnsTextHtml() {
		TypedElement input = new TypedElement(newDocument());
		
		assertThat(input.mimeType(), is("text/html; charset=UTF-8"));
	}
	
	@Test
	public void lengthReturnsByteCount() {
		TypedElement input = new TypedElement(Jsoup.parse(SHELL_HTML));
		
		assertThat(input.length(), is(49L));
	}
	
	@Test
	public void inReturnsStream() throws Exception {
		TypedElement input = new TypedElement(Jsoup.parse(SHELL_HTML));
		
		assertThat(toString(input.in()), is(SHELL_HTML));
	}
	
	@Test
	public void parseReturnsElement() throws ConversionException {
		TypedElement actual = TypedElement.parse(new TypedString(SHELL_HTML));
		
		assertThat(actual, is(new TypedElement(Jsoup.parse(SHELL_HTML))));
	}

	@Test
	public void parseWithIOExceptionThrowsException() throws IOException, ConversionException {
		TypedInput input = mock(TypedInput.class);
		when(input.in()).thenThrow(new IOException());
		
		thrown.expect(ConversionException.class);

		TypedElement.parse(input);
	}
	
	@Test
	public void equalsWithEqualReturnsTrue() {
		TypedElement input1 = new TypedElement(newDocument());
		TypedElement input2 = new TypedElement(newDocument());
		
		assertThat(input1.equals(input2), is(true));
	}
	
	@Test
	public void equalsWithUnequalElementReturnsFalse() {
		TypedElement input1 = new TypedElement(Jsoup.parse("<h1/>"));
		TypedElement input2 = new TypedElement(Jsoup.parse("<h2/>"));
		
		assertThat(input1.equals(input2), is(false));
	}
	
	@Test
	public void equalsWithNullReturnsFalse() {
		TypedElement input1 = new TypedElement(newDocument());
		TypedElement input2 = null;
		
		assertThat(input1.equals(input2), is(false));
	}
	
	@Test
	public void equalsWithDifferentTypeReturnsFalse() {
		TypedElement input = new TypedElement(newDocument());

		assertThat(input.equals(new Object()), is(false));
	}
	
	@Test
	public void hashCodeWhenEqualIsEqual() {
		TypedElement input1 = new TypedElement(newDocument());
		TypedElement input2 = new TypedElement(newDocument());
		
		assertThat(input1.hashCode(), is(input2.hashCode()));
	}

	private static Document newDocument() {
		return Document.createShell("");
	}

	private static String toString(InputStream in) throws IOException {
		return CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
	}
}
