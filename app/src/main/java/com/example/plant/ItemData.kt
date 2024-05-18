package com.example.plant

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
class ItemData {
    @field:Element(name = "adviseInfo", required = false)
    var adviseInfo: String? = null

    @field:Element(name = "plantNm", required = false)
    var plantNm: String? = null

    @field:Element(name = "clCodeNm", required = false)
    var clCodeNm: String? = null

    @field:Element(name = "cntntsNo", required = false)
    var cntntsNo: String? = null

    @field:Element(name = "distbNm", required = false)
    var distbNm: String? = null

    @field:Element(name = "dlthtsCodeNm", required = false)
    var dlthtsCodeNm: String? = null

    @field:Element(name = "dlthtsManageInfo", required = false)
    var dlthtsManageInfo: String? = null

    @field:Element(name = "eclgyCodeNm", required = false)
    var eclgyCodeNm: String? = null

    @field:Element(name = "etcEraInfo", required = false)
    var etcEraInfo: String? = null

    @field:Element(name = "flclrCodeNm", required = false)
    var flclrCodeNm: String? = null

    @field:Element(name = "flpodmtBigInfo", required = false)
    var flpodmtBigInfo: String? = null

    @field:Element(name = "flpodmtMddlInfo", required = false)
    var flpodmtMddlInfo: String? = null

    @field:Element(name = "flpodmtSmallInfo", required = false)
    var flpodmtSmallInfo: String? = null

    @field:Element(name = "fmlCodeNm", required = false)
    var fmlCodeNm: String? = null

    @field:Element(name = "fmlNm", required = false)
    var fmlNm: String? = null

    @field:Element(name = "fmldeSeasonCodeNm", required = false)
    var fmldeSeasonCodeNm: String? = null

    @field:Element(name = "fmldecolrCodeNm", required = false)
    var fmldecolrCodeNm: String? = null

    @field:Element(name = "fncltyInfo", required = false)
    var fncltyInfo: String? = null

    @field:Element(name = "frtlzrInfo", required = false)
    var frtlzrInfo: String? = null

    @field:Element(name = "growthAraInfo", required = false)
    var growthAraInfo: String? = null

    @field:Element(name = "growthHgInfo", required = false)
    var growthHgInfo: String? = null

    @field:Element(name = "grwhTpCode", required = false)
    var grwhTpCode: String? = null

    @field:Element(name = "grwhTpCodeNm", required = false)
    var grwhTpCodeNm: String? = null

    @field:Element(name = "grwhstleCodeNm", required = false)
    var grwhstleCodeNm: String? = null

    @field:Element(name = "grwtveCode", required = false)
    var grwtveCode: String? = null

    @field:Element(name = "grwtveCodeNm", required = false)
    var grwtveCodeNm: String? = null

    @field:Element(name = "hdCode", required = false)
    var hdCode: String? = null

    @field:Element(name = "hdCodeNm", required = false)
    var hdCodeNm: String? = null

    @field:Element(name = "hgBigInfo", required = false)
    var hgBigInfo: String? = null

    @field:Element(name = "hgMddlInfo", required = false)
    var hgMddlInfo: String? = null

    @field:Element(name = "hgSmallInfo", required = false)
    var hgSmallInfo: String? = null

    @field:Element(name = "ignSeasonCodeNm", required = false)
    var ignSeasonCodeNm: String? = null

    @field:Element(name = "imageEvlLinkCours", required = false)
    var imageEvlLinkCours: String? = null

    @field:Element(name = "indoorpsncpacompositionCodeNm", required = false)
    var indoorpsncpacompositionCodeNm: String? = null

    @field:Element(name = "lefStleInfo", required = false)
    var lefStleInfo: String? = null

    @field:Element(name = "lefcolrCodeNm", required = false)
    var lefcolrCodeNm: String? = null

    @field:Element(name = "lefmrkCodeNm", required = false)
    var lefmrkCodeNm: String? = null

    @field:Element(name = "lighttdemanddoCodeNm", required = false)
    var lighttdemanddoCodeNm: String? = null

    @field:Element(name = "managedemanddoCode", required = false)
    var managedemanddoCode: String? = null

    @field:Element(name = "managedemanddoCodeNm", required = false)
    var managedemanddoCodeNm: String? = null

    @field:Element(name = "managelevelCode", required = false)
    var managelevelCode: String? = null

    @field:Element(name = "managelevelCodeNm", required = false)
    var managelevelCodeNm: String? = null

    @field:Element(name = "orgplceInfo", required = false)
    var orgplceInfo: String? = null

    @field:Element(name = "pcBigInfo", required = false)
    var pcBigInfo: String? = null

    @field:Element(name = "pcMddlInfo", required = false)
    var pcMddlInfo: String? = null

    @field:Element(name = "pcSmallInfo", required = false)
    var pcSmallInfo: String? = null

    @field:Element(name = "plntbneNm", required = false)
    var plntbneNm: String? = null

    @field:Element(name = "plntzrNm", required = false)
    var plntzrNm: String? = null

    @field:Element(name = "postngplaceCodeNm", required = false)
    var postngplaceCodeNm: String? = null

    @field:Element(name = "prpgtEraInfo", required = false)
    var prpgtEraInfo: String? = null

    @field:Element(name = "prpgtmthCodeNm", required = false)
    var prpgtmthCodeNm: String? = null

    @field:Element(name = "smellCode", required = false)
    var smellCode: String? = null

    @field:Element(name = "smellCodeNm", required = false)
    var smellCodeNm: String? = null

    @field:Element(name = "soilInfo", required = false)
    var soilInfo: String? = null

    @field:Element(name = "speclmanageInfo", required = false)
    var speclmanageInfo: String? = null

    @field:Element(name = "toxctyInfo", required = false)
    var toxctyInfo: String? = null

    @field:Element(name = "volmeBigInfo", required = false)
    var volmeBigInfo: String? = null

    @field:Element(name = "volmeMddlInfo", required = false)
    var volmeMddlInfo: String? = null

    @field:Element(name = "volmeSmallInfo", required = false)
    var volmeSmallInfo: String? = null

    @field:Element(name = "vrticlBigInfo", required = false)
    var vrticlBigInfo: String? = null

    @field:Element(name = "vrticlMddlInfo", required = false)
    var vrticlMddlInfo: String? = null

    @field:Element(name = "vrticlSmallInfo", required = false)
    var vrticlSmallInfo: String? = null

    @field:Element(name = "watercycleAutumnCode", required = false)
    var watercycleAutumnCode: String? = null

    @field:Element(name = "watercycleAutumnCodeNm", required = false)
    var watercycleAutumnCodeNm: String? = null

    @field:Element(name = "watercycleSprngCode", required = false)
    var watercycleSprngCode: String? = null

    @field:Element(name = "watercycleSprngCodeNm", required = false)
    var watercycleSprngCodeNm: String? = null

    @field:Element(name = "watercycleSummerCode", required = false)
    var watercycleSummerCode: String? = null

    @field:Element(name = "watercycleSummerCodeNm", required = false)
    var watercycleSummerCodeNm: String? = null

    @field:Element(name = "watercycleWinterCode", required = false)
    var watercycleWinterCode: String? = null

    @field:Element(name = "watercycleWinterCodeNm", required = false)
    var watercycleWinterCodeNm: String? = null

    @field:Element(name = "widthBigInfo", required = false)
    var widthBigInfo: String? = null

    @field:Element(name = "widthMddlInfo", required = false)
    var widthMddlInfo: String? = null

    @field:Element(name = "widthSmallInfo", required = false)
    var widthSmallInfo: String? = null

    @field:Element(name = "winterLwetTpCode", required = false)
    var winterLwetTpCode: String? = null

    @field:Element(name = "winterLwetTpCodeNm", required = false)
    var winterLwetTpCodeNm: String? = null
}