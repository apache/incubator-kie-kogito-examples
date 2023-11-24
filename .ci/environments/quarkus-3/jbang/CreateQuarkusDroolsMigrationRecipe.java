/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.quarkus.devtools.project.BuildTool;
import io.quarkus.devtools.project.update.rewrite.QuarkusUpdateRecipe;
import io.quarkus.devtools.project.update.rewrite.QuarkusUpdateRecipeIO;
import io.quarkus.devtools.project.update.rewrite.operations.UpdatePropertyOperation;

///usr/bin/env jbang "$0" "$@" ; exit $?
// Version to be changed when needed
//DEPS io.quarkus:quarkus-devtools-common:3.5.2

/*
 * This script will generate the final `quarkus3.yml` file based on:
 *   - quarkus recipe file (see `QUARKUS_UPDATES_BASE_URL` constant)
 *   - local project-recipe.yaml => Specific project repository rules
 * 
 * We use a lot of managed dependencies, it concatenates both files but it also add some new rules:
 * In the Quarkus recipe, the dependencies rules are modified only for direct dependencies but not for managed dependencies.
 * So the script adds a new step:
 *   - Reads all modified direct dependencies from the Quarkus recipe
 *   - Generates one managed dependency rule for each of them
 */
class CreateQuarkusProjectMigrationRecipe {

    static final String QUARKUS_VERSION = "3.5.2";
    static final String QUARKUS_UPDATES_BASE_URL = "https://github.com/quarkusio/quarkus-updates/blob/main/recipes/src/main/resources/quarkus-updates/core/3.5.yaml";

    static final Path quarkus3DownloadedRecipePath = Paths.get("quarkus3-base-recipe.yml");
    static final Path quarkus3GeneratedRecipePath = Paths.get("quarkus3.yml");
    static final Path projectBaseRecipePath = Paths.get("project-recipe.yml");

    public static void main(String... args) throws Exception {
        boolean downloadQuarkusRecipe = false;
        if (args.length > 0) {
            downloadQuarkusRecipe = Boolean.parseBoolean(args[0]);
        }

        if (downloadQuarkusRecipe) {
            Files.write(quarkus3DownloadedRecipePath, new URL(QUARKUS_UPDATES_BASE_URL).openStream().readAllBytes());
        }

        if (!Files.exists(quarkus3DownloadedRecipePath)) {
            System.out.println("The Quarkus base recipe (" + quarkus3DownloadedRecipePath.getFileName()
                    + ") does not exist into the folder. Please download it manually or add the `true` parameter to the script call !");
            System.exit(1);
        }

        List<Object> quarkusRecipes = QuarkusUpdateRecipeIO
                .readRecipesYaml(Files.readString(quarkus3DownloadedRecipePath));
        QuarkusUpdateRecipe mainRecipe = new QuarkusUpdateRecipe()
                .buildTool(BuildTool.MAVEN)
                .addOperation(new UpdatePropertyOperation("version.io.quarkus", QUARKUS_VERSION));
                .addOperation(new UpdatePropertyOperation("quarkus.platform.version", QUARKUS_VERSION));
                .addOperation(new UpdatePropertyOperation("quarkus-plugin.version", QUARKUS_VERSION));

        if (Files.exists(projectBaseRecipePath)) {
            System.out.println("Adding Project base recipe(s)");
            mainRecipe.addRecipes(QuarkusUpdateRecipeIO.readRecipesYaml(Files.readString(projectBaseRecipePath)));
        } else {
            System.out.println("No Project base recipe(s) available. Nothing done here ...");
        }

        System.out.println("Adding Managed dependency recipe(s)");
        Map<String, Object> managedDependencyMainRecipe = Map.of(
                "type", "specs.openrewrite.org/v1beta/recipe",
                "name", "org.kie.ManagedDependencies",
                "displayName", "Update Managed Dependencies",
                "description", "Update all managed dependencies based on dependency updates from Quarkus.",
                "recipeList", retrieveAllChangeDependencyRecipesToManagedDependency(quarkusRecipes));
        mainRecipe.addRecipe(managedDependencyMainRecipe);

        System.out.println("Adding Quarkus base recipe(s)");
        mainRecipe.addRecipes(quarkusRecipes);

        System.out.println("Writing main recipe");
        QuarkusUpdateRecipeIO.write(quarkus3GeneratedRecipePath, mainRecipe);
    }

    private static List<Object> retrieveAllChangeDependencyRecipesToManagedDependency(List<Object> recipes) {
        List<Object> changeDependencyRecipeList = new ArrayList<>();
        recipes.forEach(r -> {
            if (r instanceof Map) {
                List<Object> recipeList = (List<Object>) ((Map<String, Object>) r).get("recipeList");
                recipeList.forEach(recipeMap -> {
                    if (recipeMap instanceof Map) {
                        ((Map<String, Map<String, Object>>) recipeMap).forEach((recipeName, args) -> {
                            if (recipeName.equals("org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId")) {
                                args.remove("overrideManagedVersion");
                                if (!args.containsKey("newArtifactId")) {
                                    args.put("newArtifactId", args.get("oldArtifactId"));
                                }
                                changeDependencyRecipeList.add(Map
                                        .of("org.openrewrite.maven.ChangeManagedDependencyGroupIdAndArtifactId", args));
                            }
                        });
                    }
                });
            }
        });
        return changeDependencyRecipeList;
    }
}
