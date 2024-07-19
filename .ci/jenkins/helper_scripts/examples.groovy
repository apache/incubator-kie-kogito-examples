/**
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
import groovy.util.XmlSlurper

List getDeployableArtifactIds() {
    def pomFiles = sh(returnStdout:true, script: 'find . -name pom.xml').trim().split('\n')
    // echo "${pomFiles}"

    def rootFolder = [
        name: 'root',
        subFolders: []
    ]
    pomFiles.each { pomFilePath ->
        def split = pomFilePath.split('/')
        def folder = rootFolder
        String path = ''
        for (int i = 0; i < split.size(); i++) {
            def name = split[i]
            if (name == 'pom.xml') {
                folder.containsPom = true
            } else {
                path += "${name}/"
                def subFolder = folder.subFolders.find { it.name == name }
                if (!subFolder) {
                    subFolder = [ name: name, subFolders: [] ]
                    folder.subFolders.add(subFolder)
                    subFolder.path = path
                }
                folder = subFolder
            }
        }
    }

    // echo "${writeJSON(json: rootFolder, returnText:true)}"
    
    return getPackagingPomProjectPaths(rootFolder).collect {
        return getArtifactId(it, readFile(file: it))
    }.findAll { it }
}

List getPackagingPomProjectPaths(def folder) {
    List paths = []
    if (folder.subFolders.size() > 0 && folder.containsPom) {
        paths.add("${folder.path}pom.xml")
    }

    folder.subFolders.each { it ->
        paths.addAll(getPackagingPomProjectPaths(it))
    }
    return paths
}

@NonCPS
String getArtifactId(String pomFileName, String pomFileContent) {
    def pomXml = new XmlSlurper().parseText(pomFileContent)
    if (pomFileName == './pom.xml' || pomXml.children().find{ it.name() == 'parent' }){
        return "${pomXml.artifactId.text()}"
    }
    println "file ignored"
    return ''
}

return this;