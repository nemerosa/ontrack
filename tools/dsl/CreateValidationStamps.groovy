ontrack.project('Test') {
   branch('trunk') {
      (1..20).each { id ->
         validationStamp "STAMP.${id}", "Validation stamp ${id}"
      }
      (1..5).each { id ->
         build "${id}"
      }
   }
}
