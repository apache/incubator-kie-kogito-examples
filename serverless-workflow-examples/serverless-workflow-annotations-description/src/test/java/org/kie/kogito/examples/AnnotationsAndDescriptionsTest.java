/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class AnnotationsAndDescriptionsTest {

    public static Stream<Arguments> testAnnotationsSource() {
        return Stream.of(
                Arguments.of("JsonannotationsResource.java", "jsonannotations"),
                Arguments.of("YamlannotationsResource.java", "yamlannotations"));
    }

    @ParameterizedTest
    @MethodSource("testAnnotationsSource")
    void testAnnotations(String fileName, String worfklowId) throws IOException {
        Path generatedFile = Paths.get("target", "generated-sources", "kogito", "org", "kie",
                "kogito", "serverless", fileName);

        CompilationUnit compilationUnit = StaticJavaParser.parse(generatedFile);

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class).get(0);

        Stream<AnnotationExpr> tagAnnotations = classOrInterfaceDeclaration.getAnnotations().stream()
                .filter(a -> a.getNameAsString().equals("Tag"));

        assertThat(tagAnnotations).containsExactlyInAnyOrder(
                createTagAnnotation("Cogito"),
                createTagAnnotation("ergo"),
                createTagAnnotation("sum"),
                createTagAnnotation(worfklowId, "This is an amazing workflow"));
    }

    private static AnnotationExpr createTagAnnotation(String name, String description) {
        NodeList<MemberValuePair> attributes = new NodeList<>();

        if (name != null) {
            attributes.add(new MemberValuePair("name", new StringLiteralExpr(name)));
        }

        if (description != null) {
            attributes.add(new MemberValuePair("description", new StringLiteralExpr(description)));
        }

        return new NormalAnnotationExpr(new Name("Tag"), attributes);
    }

    private static AnnotationExpr createTagAnnotation(String name) {
        return createTagAnnotation(name, null);
    }
}
