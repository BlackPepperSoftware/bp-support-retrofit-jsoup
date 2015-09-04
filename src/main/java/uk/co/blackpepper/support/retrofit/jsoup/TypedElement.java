package uk.co.blackpepper.support.retrofit.jsoup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import retrofit.converter.ConversionException;
import retrofit.mime.TypedInput;

import static com.google.common.base.Preconditions.checkNotNull;

public class TypedElement implements TypedInput {

	private final Element element;

	public TypedElement(Element element) {
		this.element = checkNotNull(element, "element");
	}

	public Element getElement() {
		return element;
	}

	@Override
	public String mimeType() {
		return "text/html; charset=UTF-8";
	}

	@Override
	public long length() {
		return getBytes().length;
	}

	@Override
	public InputStream in() throws IOException {
		return new ByteArrayInputStream(getBytes());
	}

	@Override
	public int hashCode() {
		return element.outerHtml().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TypedElement)) {
			return false;
		}
		
		TypedElement that = (TypedElement) object;
		
		return element.outerHtml().equals(that.getElement().outerHtml());
	}

	public static TypedElement parse(TypedInput input) throws ConversionException {
		if (input instanceof TypedElement) {
			return ((TypedElement) input);
		}
		
		Document document;
		try {
			document = Jsoup.parse(input.in(), StandardCharsets.UTF_8.name(), "");
		}
		catch (IOException exception) {
			throw new ConversionException("Error parsing document", exception);
		}
		
		return new TypedElement(document);
	}

	private byte[] getBytes() {
		return element.outerHtml().getBytes(StandardCharsets.UTF_8);
	}
}
