(function () {
    if (typeof window.jQuery === "undefined") {
        return;
    }

    var ACTIVIDADES = [
        "0111 - Cultivo de cereales",
        "0113 - Cultivo de hortalizas y melones",
        "0141 - Cria de ganado bovino",
        "1010 - Procesamiento y conservacion de carne",
        "1071 - Elaboracion de productos de panaderia",
        "1104 - Elaboracion de bebidas no alcoholicas",
        "1313 - Acabado de productos textiles",
        "1410 - Confeccion de prendas de vestir",
        "1622 - Fabricacion de partes de madera",
        "1811 - Impresion",
        "2023 - Fabricacion de jabones y detergentes",
        "2220 - Fabricacion de productos de plastico",
        "2395 - Fabricacion de articulos de concreto",
        "2599 - Fabricacion de otros productos elaborados de metal",
        "3100 - Fabricacion de muebles",
        "4100 - Construccion de edificios",
        "4321 - Instalaciones electricas",
        "4510 - Venta de vehiculos automotores",
        "4620 - Comercio al por mayor de materias primas agropecuarias",
        "4711 - Venta al por menor en comercios no especializados",
        "4721 - Venta al por menor de alimentos en comercios especializados",
        "4752 - Venta al por menor de ferreteria y materiales",
        "5610 - Actividades de restaurantes",
        "6201 - Actividades de programacion informatica",
        "6920 - Actividades de contabilidad",
        "7020 - Actividades de consultoria de gestion",
        "8299 - Otras actividades de servicios de apoyo"
    ];

    var CATALOGO_ELSALVADOR = {
        "Ahuachapan": ["Ahuachapan", "Apaneca", "Atiquizaya", "Concepcion de Ataco", "Jujutla"],
        "Santa Ana": ["Santa Ana", "Chalchuapa", "Coatepeque", "El Congo", "Metapan"],
        "Sonsonate": ["Sonsonate", "Acajutla", "Armenia", "Nahuizalco", "Izalco"],
        "Chalatenango": ["Chalatenango", "Dulce Nombre de Maria", "La Palma", "Nueva Concepcion", "Tejutla"],
        "La Libertad": ["Santa Tecla", "Antiguo Cuscatlan", "Colon", "Comasagua", "Quezaltepeque"],
        "San Salvador": ["San Salvador", "Apopa", "Ilopango", "Mejicanos", "Soyapango"],
        "Cuscatlan": ["Cojutepeque", "Suchitoto", "San Pedro Perulapan", "San Rafael Cedros", "Tenancingo"],
        "La Paz": ["Zacatecoluca", "Olocuilta", "San Luis Talpa", "Santiago Nonualco", "San Pedro Masahuat"],
        "Cabanas": ["Sensuntepeque", "Ilobasco", "Tejutepeque", "Victoria", "Cinquera"],
        "San Vicente": ["San Vicente", "Apastepeque", "Tecoluca", "San Sebastian", "Verapaz"],
        "Usulutan": ["Usulutan", "Jiquilisco", "Puerto El Triunfo", "Santiago de Maria", "Berlin"],
        "San Miguel": ["San Miguel", "Chirilagua", "Moncagua", "Chinameca", "El Transito"],
        "Morazan": ["San Francisco Gotera", "Cacaopera", "Corinto", "Jocoaitique", "Sociedad"],
        "La Union": ["La Union", "Conchagua", "Intipuca", "Pasaquina", "Santa Rosa de Lima"]
    };

    var $actividad = jQuery("#actividadEconomica");
    var $pais = jQuery("#pais");
    var $departamento = jQuery("#departamento");
    var $municipio = jQuery("#municipio");

    if ($actividad.length === 0 || $pais.length === 0 || $departamento.length === 0 || $municipio.length === 0) {
        return;
    }

    function initSelect2($el, placeholder) {
        if (!$el.hasClass("select2-hidden-accessible")) {
            $el.select2({
                theme: "default",
                width: "100%",
                placeholder: placeholder,
                allowClear: true,
                dropdownCssClass: "select2-mazer-dropdown"
            });
        }
    }

    function fillActividad() {
        var current = $actividad.attr("data-current") || $actividad.val() || "";
        $actividad.find("option:not(:first)").remove();
        ACTIVIDADES.forEach(function (item) {
            var option = new Option(item, item, false, item === current);
            $actividad.append(option);
        });
        $actividad.val(current).trigger("change.select2");
    }

    function fillDepartamentos() {
        var pais = ($pais.val() || "").trim();
        var currentDept = $departamento.attr("data-current") || $departamento.val() || "";

        $departamento.find("option:not(:first)").remove();
        $municipio.find("option:not(:first)").remove();

        if (pais !== "El Salvador") {
            $departamento.val("").trigger("change.select2");
            $municipio.val("").trigger("change.select2");
            return;
        }

        Object.keys(CATALOGO_ELSALVADOR).forEach(function (d) {
            var option = new Option(d, d, false, d === currentDept);
            $departamento.append(option);
        });

        $departamento.val(currentDept).trigger("change.select2");
        fillMunicipios();
    }

    function fillMunicipios() {
        var dept = ($departamento.val() || "").trim();
        var currentMunicipio = $municipio.attr("data-current") || $municipio.val() || "";

        $municipio.find("option:not(:first)").remove();

        var municipios = CATALOGO_ELSALVADOR[dept] || [];
        municipios.forEach(function (m) {
            var option = new Option(m, m, false, m === currentMunicipio);
            $municipio.append(option);
        });

        $municipio.val(currentMunicipio).trigger("change.select2");
    }

    initSelect2($actividad, "Seleccionar actividad economica");
    initSelect2($pais, "Seleccionar pais");
    initSelect2($departamento, "Seleccionar departamento");
    initSelect2($municipio, "Seleccionar municipio");

    fillActividad();
    fillDepartamentos();

    $pais.on("change", function () {
        $departamento.attr("data-current", "");
        $municipio.attr("data-current", "");
        fillDepartamentos();
    });

    $departamento.on("change", function () {
        $municipio.attr("data-current", "");
        fillMunicipios();
    });
})();
