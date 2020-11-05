package com.example.utils;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

public class MarkdownToHtmlUtil {

    public static String markdownToHtml(String markdown){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static String markdownToHtmlExtensions(String markdown){
        Set<Extension> headExtensions = Collections.singleton(HeadingAnchorExtension.create());
        List<Extension> tableExtensions = Arrays.asList((Extension)TablesExtension.create());

        Parser parser = Parser.builder().extensions(tableExtensions).build();

        Node document = parser.parse(markdown);

        HtmlRenderer htmlRenderer = HtmlRenderer.builder()
                .extensions(tableExtensions)
                .extensions(headExtensions)
                .attributeProviderFactory(new AttributeProviderFactory(){

                    @Override
                    public AttributeProvider create(AttributeProviderContext attributeProviderContext) {
                        return new Attribute();
                    }
                }).build();

        return htmlRenderer.render(document);
    }

    static class Attribute implements AttributeProvider{

        @Override
        public void setAttributes(Node node, String s, Map<String, String> map) {
            if(node instanceof Link){
                map.put("target","_blank");
            }
            if(node instanceof TableBlock){
                map.put("class","ui celled table");
            }
            if(node instanceof IndentedCodeBlock){
                map.put("class","language-css");
            }
            if(node instanceof Code){
                map.put("class","language-css");
            }
            if(node instanceof FencedCodeBlock){
                map.put("class","language-css");
            }
        }
    }




}
