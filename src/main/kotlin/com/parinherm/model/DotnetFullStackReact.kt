//import com.github.jknack.handlebars.Handlebars
//import com.github.jknack.handlebars.Template
import com.parinherm.ApplicationData
import com.parinherm.entity.ViewDefinition
import com.parinherm.form.definitions.ViewDef
import com.parinherm.model.PebbleTemplateExpansion
import com.parinherm.server.DefaultViewDefinitions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths

val tempOutputDirectory = "${ApplicationData.userPath}${File.separator}"

fun generateDotNetFullStackReact(viewDefinitions: List<ViewDefinition>){
    MainScope().launch(Dispatchers.Main) {
        viewDefinitions.forEach { viewDefinition: ViewDefinition ->
           val viewDef = DefaultViewDefinitions.loadView(viewDefinition.viewId)
           generateAll(viewDef)
        }
    }
}

/*
fun generateDotNetFullStackReact(viewDefs: List<ViewDef>){
    MainScope().launch(Dispatchers.Main) {

        viewDefs.forEach { viewDef: ViewDef ->
            //generateEndpoint(ApplicationData.handleBarsEngine, viewDef)
            //generateDTO(ApplicationData.handleBarsEngine, viewDef)
            //generateRepository(ApplicationData.handleBarsEngine, viewDef)
            //generateReact(ApplicationData.handleBarsEngine, viewDef)
            generateAll(viewDef)
        }

    }
}
 */

fun generateAll(viewDef: ViewDef){
    //val basepath =  "templates/plugin/dotnet/"
    val basepath = ""
    val templates = listOf(PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}dto.peb"),
        ApplicationData.makeCapital(viewDef.entityDef.name) + "DTO.cs",
        listOf("DTO")
    ),
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}viewModel.peb"),
        ApplicationData.makeCapital(viewDef.entityDef.name) + "ViewModel.cs",
        listOf("DTO")
    ),
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}endpoint.peb"),
        ApplicationData.makeCapital(viewDef.entityDef.name) + "Endpoints.cs",
        listOf("Endpoints")
    ),
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}repositoryInterface.peb"),
        "I${ApplicationData.makeCapital(viewDef.entityDef.name)}Repository.cs",
        listOf("Repository")
    ),
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}repositoryClass.peb"),
        ApplicationData.makeCapital(viewDef.entityDef.name) + "Repository.cs",
        listOf("Repository")
    ),
   PebbleTemplateExpansion(
            viewDef,
            ApplicationData.pebbleEngine.getTemplate("${basepath}efmodel.peb"),
            ApplicationData.makeCapital(viewDef.entityDef.name) + ".cs",
            listOf("Model")
    ),
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}form.peb"),
"Form.jsx",
        listOf("React", viewDef.id)
    ),
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}formDialog.peb"),
        "FormDialog.jsx",
        listOf("React", viewDef.id)
    ))
    for(template in templates){
        writeTemplate(template)
    }
}

/*
,
    PebbleTemplateExpansion(
        viewDef,
        ApplicationData.pebbleEngine.getTemplate("${basepath}functions.peb"),
        "functions.js",
        listOf("React", viewDef.id)
    )
 */

/*
suspend fun generateEndpoint(hbars: Handlebars, viewDef: ViewDef){
    val template = hbars.compile("/dotnet/endpoint")
    writeTemplate(viewDef, template, ApplicationData.makeCapital(viewDef.entityDef.name) + "Endpoints.cs",  "Endpoints")
}

suspend fun generateRepository(hbars: Handlebars, viewDef: ViewDef){
    val interfaceTemplate = hbars.compile("/dotnet/repositoryInterface")
    writeTemplate(viewDef, interfaceTemplate, "I" + ApplicationData.makeCapital(viewDef.entityDef.name) + "Repository.cs",  "Repository")

    val repositoryTemplate = hbars.compile("/dotnet/repositoryClass")
    writeTemplate(viewDef, repositoryTemplate, ApplicationData.makeCapital(viewDef.entityDef.name) + "Repository.cs",  "Repository")
}

suspend fun generateDTO(hbars: Handlebars, viewDef: ViewDef){
    val template = hbars.compile("/dotnet/dto")
    writeTemplate(viewDef, template, ApplicationData.makeCapital(viewDef.entityDef.name) + "DTO.cs",  "DTO")
}
suspend fun generateReact(hbars: Handlebars, viewDef: ViewDef){
    val formTemplate = hbars.compile("/dotnet/form")
    writeTemplate(viewDef, formTemplate, "Form.jsx",  "React")

    val functionsTemplate = hbars.compile("/dotnet/functions")
    writeTemplate(viewDef, functionsTemplate, "functions.js",  "React")
}

fun writeTemplate(viewDef: ViewDef, template: Template, fileName: String, vararg folders: String ) {
    val tempOutputPath = Paths.get(tempOutputDirectory, "models", viewDef.id, *folders)
    Files.createDirectories(tempOutputPath)
    val filePath = tempOutputPath.resolve(fileName)
    File(filePath.toString()).writeText(template.apply(viewDef))
}
 */

fun writeTemplate(pebbleTemplate: PebbleTemplateExpansion) {
    val tempOutputPath = Paths.get(tempOutputDirectory, "models", *pebbleTemplate.folders.toTypedArray())
    Files.createDirectories(tempOutputPath)
    val filePath = tempOutputPath.resolve(pebbleTemplate.fileName)

    val writer = StringWriter();
    val context = mutableMapOf<String, Any?>()
    context["viewDef"] = pebbleTemplate.viewDef
    pebbleTemplate.template.evaluate(writer, context)
    val output = writer.toString()

    File(filePath.toString()).writeText(output)
}