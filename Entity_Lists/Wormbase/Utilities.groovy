import java.util.zip.GZIPInputStream

def static download(String input, String output) {
    String inputFile = input.substring(input.lastIndexOf("/"))
    File tempDir = File.createTempDir()
    File tempFile = new File(tempDir.absolutePath+"/"+inputFile)
    def outputStream = tempFile.newDataOutputStream()
    println "start download"
    outputStream << new URL(input).openStream()
    outputStream.close()

    def file = new File(output)
    if(file.exists()){
        assert file.delete()
    }
    assert tempFile.renameTo(file)

    println "finish download"
}

def static gunzip(String file_input, String file_output) {

    println "start gunzip ${file_input} -> ${file_output}"

    File inputFile = new File(file_input)
    File outputFile = new File(file_output)

    if(!inputFile.exists()){
        throw new RuntimeException("input file does not exists: "+file_input)
    }

    if(outputFile.exists()){
        assert outputFile.delete()
    }

    FileInputStream fis = new FileInputStream(file_input)
    FileOutputStream fos = new FileOutputStream(file_output)
    GZIPInputStream gzis = new GZIPInputStream(fis)
    byte[] buffer = new byte[1024]
    int len = 0

    while ((len = gzis.read(buffer)) > 0) {
        fos.write(buffer, 0, len)
    }

    fos.close()
    fis.close()
    gzis.close()
    println "finish gunzip ${file_input} -> ${file_output}"
}
