import java.util.zip.GZIPInputStream


def gunzip(String file_input, String file_output) {
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
}

if (args == null || args.length == 0) {
    println "Proper usage is: groovy ingest_taxonomy_file.groovy namesFile.gz speciesIdFile"
} else {

def inputFile = args[0]

def unzipped_input = "names.dmp"
def final_output = "species_names_lexica.txt"
File speciesFile = new File(args[1])

File ourOutputFile = new File(final_output)
removedParsedFile = ourOutputFile.delete()

File unzippedFile = new File(unzipped_input)

if (!unzippedFile.exists()){
   gunzip(inputFile,unzipped_input)
}

File inputFileToParse = new File(unzipped_input)
speciesToParse = []
speciesFile.eachLine {
	speciesToParse.add(it)
}
def speciesNames = [:]
def nameMap = [:]
def name = ""
def computedNameMap = [:]

inputFileToParse.eachLine {
    lineContent = it.split('\\|')
    taxonId = lineContent[0].trim()
  
    if (taxonId in speciesToParse){
         nameClass = lineContent[3].trim()

    	 if (nameClass.trim() == "scientific name"){
       	    name = lineContent[1].trim().toString()
	    
	    nameMap[taxonId]=name
	    computedNamePrefix = name.substring(0,1)+"."
	    computedNameSuffix = name.substring(name.lastIndexOf(" ") + 1)
	    computedName = computedNamePrefix + " " + computedNameSuffix
	    computedNameMap[taxonId]=computedName

    	 }

    	 synonym = lineContent[2].trim()

	 anotherName = lineContent[1].trim()
     	 taxonInMap = speciesNames.get(taxonId)

	 if (taxonInMap != null) {
	    if (synonym.trim() != "" && synonym.trim() != null){ 
	       synonyms.add(synonym.trim())
	       speciesNames[taxonId] = synonyms
	    }
	    if (anotherName.trim() != "" && anotherName.trim() != null && anotherName != name) {
	       synonyms.add(anotherName.trim())
	       speciesNames[taxonId] = synonyms
	    }
	 } 
	 else {
	      synonyms = []
	      name = ""
	      computedName = ""
	      if (synonym.trim() != "" && synonym.trim() != null){
	      	 synonyms.add(synonym.trim())
	      	 speciesNames.put(taxonId,synonyms)
	      }
	      if (anotherName.trim() != "" && anotherName.trim() != null){
	      	 synonyms.add(anotherName.trim())
		 speciesNames.put(taxonId,synonyms)
	      }
	   }
         } 
    }

for (taxid in nameMap) {
    ourOutputFile << taxid.key+'\t'+taxid.value+'\t'
    for (spec in speciesNames){
    	if (spec.key == taxid.key){
	    specsyn = spec.value.join("|")
	    ourOutputFile << specsyn+'\t'
	}
    }
    for (cn in computedNameMap) {
    	if (cn.key == taxid.key){
	   ourOutputFile << '|' + cn.value + '\t\t' + '\n'	
    	}
    }
}

}
