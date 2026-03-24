package com.example.aspirepath

data class Exam(
    val name: String,
    val purpose: String = "",
    val eligibility: String = "",
    val examDate: String = "",
    val applyMode: String = "",
    val website: String
)

object ExamData {
    val medical = listOf(
        Exam("NEET-UG", "Admission to MBBS / BDS / B.V.Sc", "Class 12 with PCB", "January / February", "Online", "https://neet.nta.nic.in/"),
        Exam("AIPVT (via NEET)", "Bachelor of Veterinary Science", "Class 12 with PCB", "January / February", "Online", "https://vci.admissions.nic.in/")
    )

    val nationalMedical = listOf(
        Exam("NEET-UG (All India Quota)", "Admission to AIIMS, JIPMER & Central Universities", "Class 12 with English & PCB", "January / March", "Online", "https://neet.nta.nic.in/"),
        Exam("CMC Vellore / Ludhiana", "Admission to MBBS (via NEET Score)", "Class 12 with English & PCB", "April / May", "Online", "https://admissions.cmc.ac.in/"),
        Exam("Manipal MET", "Admission to Health Sciences", "Class 12 with PCB", "January / March", "Online", "https://www.manipal.edu/"),
        Exam("MGIMS Wardha", "Admission to MBBS (via NEET Score)", "Class 12 with PCB", "Year-round", "Online", "https://www.mgims.ac.in/")
    )

    val engineering = listOf(
        Exam("JEE Main", "Admission in B.E / B.Tech / B.Planning", "Class 12", "November / December", "Online", "https://jeemain.nta.nic.in/"),
        Exam("JEE Advanced", "Admission in IITs", "Class 12 with PCM (Must qualify JEE Main)", "April / May", "Online", "https://jeeadv.ac.in/"),
        Exam("BITSAT", "Admission in BITS Pilani, Goa & Hyderabad", "Class 12 with PCM", "January / February", "Online", "https://www.bitsadmission.com/")
    )

    val nationalEngineering = listOf(
        Exam("VITEEE", "Admission to VIT University", "Class 12 with PCM/PCB", "November / March", "Online", "https://viteee.vit.ac.in/"),
        Exam("SRMJEEE", "Admission to SRM Institute", "Class 12 with PCM/PCB", "November / March", "Online", "https://www.srmist.edu.in/"),
        Exam("COMEDK UGET", "Admission to Engineering (Karnataka)", "Class 12 with PCM", "February / April", "Online", "https://www.comedk.org")
    )

    val architectureAndDesign = listOf(
        Exam("NATA", "Bachelor of Architecture (B.Arch)", "Class 12 with PCM", "March / July", "Online", "https://www.nata.in/"),
        Exam("JEE Main Paper 2", "B.Arch / B.Planning in NITs/SPAs", "Class 12 with PCM", "November / December", "Online", "https://jeemain.nta.nic.in/"),
        Exam("UCEED", "Bachelor of Design (IITs/IIITs)", "Class 12 (Any stream)", "October / November", "Online", "https://www.uceed.iitb.ac.in/"),
        Exam("NID DAT", "B.Des in National Institute of Design", "Class 12 (Any stream)", "October / December", "Online", "https://admissions.nid.edu/"),
        Exam("CEPT Entrance", "Bachelor of Design / Interior Design", "Class 12", "February / March", "Online", "https://admissions.cept.ac.in/")
    )

    val fashion = listOf(
        Exam("NIFT Entrance Exam", "B.Des / B.FTech in Fashion", "Class 12", "November / December", "Online", "https://nift.nta.ac.in/"),
        Exam("Pearl Academy Entrance", "Fashion Design / Styling", "Class 12", "Year-round", "Online", "https://pearlacademy.com/"),
        Exam("SOFT Entrance", "School of Fashion Technology", "Class 12", "April / May", "Online", "https://softpune.com/"),
        Exam("AIEED", "All India Entrance Examination for Design", "Class 10+2", "Year-round", "Online", "https://www.archedu.org/")
    )

    val fineArts = listOf(
        Exam("CUET-UG (Fine Arts)", "BFA in Central Universities", "Class 12", "February / March", "Online", "https://cuet.samarth.ac.in/"),
        Exam("JJ School of Art Entrance", "Bachelor of Fine Arts (BFA)", "Class 12", "March / April", "Online", "https://sas.maharashtracet.org/"),
        Exam("Srishti Manipal", "BFA in Creative Arts", "Class 12", "February", "Online", "https://srishtimanipalinstitute.in/")
    )

    val languages = listOf(
        Exam("CUET-UG", "Admission to JNU, EFLU, etc.", "Class 12", "February / March", "Online", "https://cuet.samarth.ac.in/"),
        Exam("EFLU Entrance", "BA (Hons) English & Foreign Languages", "Class 12", "February", "Online", "https://www.efluniversity.ac.in/")
    )

    val law = listOf(
        Exam("CLAT", "Admission in National Law Universities", "Class 12", "August / October", "Online", "https://consortiumofnlus.ac.in/"),
        Exam("LSAT-India", "Admission in Law Colleges", "Class 12", "Year-round", "Online", "https://www.lsatindia.in/"),
        Exam("AILET", "Admission in NLU Delhi", "Class 12", "August / September", "Online", "https://nationallawuniversitydelhi.in/")
    )

    val humanities = listOf(
        Exam("HSEE (IIT Madras)", "Integrated MA Programme", "Class 12", "December", "Online", "https://hsee.iitm.ac.in/"),
        Exam("TISS-BAT (via CUET)", "BA Social Sciences", "Class 12", "February", "Online", "https://cuet.samarth.ac.in/"),
        Exam("Ashoka Aptitude Test", "Liberal Arts & Sciences", "Class 12", "Year-round", "Online", "https://www.ashoka.edu.in/")
    )

    val banking = listOf(
        Exam("IBPS PO/Clerk", "Nationalized Banks Recruitment", "Graduation", "August / October", "Online", "https://www.ibps.in/"),
        Exam("SBI PO/Clerk", "State Bank Recruitment", "Graduation", "September / November", "Online", "https://bank.sbi/careers")
    )

    val commerce = listOf(
        Exam("ICAI CA Foundation", "Chartered Accountancy", "Class 12", "Year-round", "Online", "https://www.icai.org/"),
        Exam("ICSI CS Foundation", "Company Secretary", "Class 12", "Year-round", "Online", "https://www.icsi.edu/"),
        Exam("CMA Foundation", "Cost Accountancy", "Class 12", "Year-round", "Online", "https://icmai.in/")
    )

    val defence = listOf(
        Exam("NDA & NA", "Army, Navy, Air Force Officers", "Class 12", "Dec / June", "Online", "https://www.upsc.gov.in/"),
        Exam("Indian Navy B.Tech Entry", "Technical Officer Entry", "Class 12 with PCM", "June / December", "Online", "https://www.joinindiannavy.gov.in/"),
        Exam("TES (Indian Army)", "Technical Entry Scheme", "Class 12 with PCM", "May / October", "Online", "https://joinindianarmy.nic.in/"),
        Exam("IMU CET", "Nautical Science / Marine Engineering", "Class 12 with PCM", "April / May", "Online", "https://www.imu.edu.in/")
    )

    fun getExams(category: String): List<Exam> {
        return when (category) {
            "Medical" -> medical
            "National Level Medical Entrance Exams" -> nationalMedical
            "Engineering" -> engineering
            "National Level Engineering Entrance Exams" -> nationalEngineering
            "Architecture and Design" -> architectureAndDesign
            "Fashion" -> fashion
            "Fine Arts" -> fineArts
            "Languages" -> languages
            "Law" -> law
            "Humanities and Social Sciences" -> humanities
            "Banking" -> banking
            "Commerce" -> commerce
            "Defence / Marine" -> defence
            else -> emptyList()
        }
    }
}