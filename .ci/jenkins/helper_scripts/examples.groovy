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