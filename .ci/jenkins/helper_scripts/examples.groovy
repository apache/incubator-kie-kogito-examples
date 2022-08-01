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

    def pomProjectPaths = getPackagingPomProjectPaths(rootFolder)
    return pomProjectPaths.collect {
        return getArtifactId(readFile(file: it))
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
String getArtifactId(String pomFileContent) {
    def pomXml = new XmlSlurper().parseText(pomFileContent)
    if (pomXml.children().find{ it.name() == 'parent' || it == './pom.xml' }){
        return "${pomXml.artifactId.text()}"
    }
    return ''
}

return this;