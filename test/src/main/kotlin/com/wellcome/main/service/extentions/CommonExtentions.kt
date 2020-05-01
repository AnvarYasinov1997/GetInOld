package com.wellcome.main.service.extentions

import com.wellcome.main.entity.Locality
import com.wellcome.main.entity.institution.DayOfWeeks
import com.wellcome.main.entity.institution.InstitutionTag
import com.wellcome.main.entity.institution.InstitutionWorkTime
import com.wellcome.main.entity.institution.MapsInstitution
import org.apache.poi.xssf.usermodel.XSSFRow
import org.jsoup.Jsoup

fun MapsInstitution.toPhones(): List<String> {
    val resPhones = mutableListOf<String>()
    this.phones?.let {
        resPhones.addAll(it.split(",")
            .map(String::trim))
    }
    return resPhones
}

fun XSSFRow.createInstitutionFromRow(locality: Locality): MapsInstitution {
    return MapsInstitution(
        name = this.getCell(0).stringCellValue,
        locality = locality,
        address = this.getCell(2)?.stringCellValue,
        phones = this.getCell(5)?.stringCellValue,
        emails = this.getCell(6)?.stringCellValue ?: this.getCell(7)?.stringCellValue,
        types = this.getCell(10)?.stringCellValue,
        cite = this.getCell(8)?.stringCellValue,
        paymentTypes = this.getCell(11)?.stringCellValue,
        vk = this.getCell(12)?.stringCellValue,
        facebook = this.getCell(14)?.stringCellValue,
        instagram = this.getCell(19)?.stringCellValue,
        lat = this.getCell(20).stringCellValue.toDouble(),
        lon = this.getCell(21).stringCellValue.toDouble()
    )
}

fun List<String>.createPartnerPicturesList(): List<String> {
    val partnerPictures = mutableListOf<String>()
    this.forEach {
        Jsoup.connect("http://bishkek.kipyat.com/ru/photos/show/$it").get()
            .select("figure")
            .forEach { figures ->
                figures.select("a")
                    .forEach { a ->
                        a.attr("href").let { ref ->
                            partnerPictures.add("http://bishkek.kipyat.com$ref")
                        }
                    }
            }
    }
    return partnerPictures
}