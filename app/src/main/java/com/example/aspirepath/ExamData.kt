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
        Exam("All India Pre Medical / Pre Dental Entrance Test (AIPMT)", "Admission to MBBS / BDS", "Class 12 with PCB", "December / January", "Online", "http://aipmt.nic.in/aipmt/welcome.aspx"),
        Exam("All India Pre Veterinary Test (AIPVT)", "Admission in Bachelor of Veterinary Science and Animal Husbandry (B.V.Sc & A.H) degree courses", "Class 12 with PCB", "January / February", "Online / Post", "www.vci.nic.in/forms/default.aspx")
    )

    val nationalMedical = listOf(
        Exam("All India Institute of Medical Sciences (AIIMS)", "Admission to MBBS", "Class 12 with English & PCB", "June / July", "Online", "http://admissions.mccvelore.ac.in/"),
        Exam("Christian Medical College, Ludhiana (CMC, Ludhiana)", "Admission to MBBS", "Class 12 with English & PCB", "June / July", "Online", "http://cmcludhiana.in/"),
        Exam("Consortium of Medical, Engineering and Dental Colleges of Karnataka (COMEDK)", "Admission to MBBS", "Class 12 with English & PCB", "May", "Online", "www.comedk.org"),
        Exam("Jawaharlal Institute of Postgraduate Medical Education and Research (JIPMER)", "Admission to MBBS", "Class 12 with PCB", "June", "Online", "http://jipmer.edu.in/"),
        Exam("Manipal (MBBS)", "Admission to MBBS", "Class 12 with PCB", "June", "Online", "www.admissions.manipal.edu"),
        Exam("Mahatma Gandhi Institute of Medical Sciences (MGIMS, Wardha)", "Admission to MBBS", "Class 12 with PCB", "June / August", "Online", "www.mgims.ac.in")
    )

    val engineering = listOf(
        Exam("Joint Entrance Examination (JEE) Main", "Admission in B.E / B.Tech, B.Arch, B.Planning", "Class 12", "November / December", "Online", "http://jeemain.nic.in/jeemainapp/welcome.aspx"),
        Exam("JEE Advance", "Admission in UG programmes in IITs and ISM Dhanbad", "Class 12 with PCM", "May", "Online", "http://jeeadv.iitd.ac.in/"),
        Exam("BITSAT", "Admission in integrated First Degree programmes in BITS Pilani, Goa & Hyderabad", "Class 12 with PCM", "January / February", "Online", "www.bitsadmission.com")
    )

    val nationalEngineering = listOf(
        Exam("COMEDK", "Admission in Engineering & Architecture courses", "Class 12", "May", "Online", "www.comedk.org"),
        Exam("Manipal", "Admission to B.Tech courses", "Class 12", "May", "Online / By Post", "www.admissions.manipal.edu"),
        Exam("AMUEEE", "Admission in Engineering courses", "Class 12", "April / May", "Online", "www.amucontrollerexams.com")
    )

    val fashion = listOf(
        Exam("Srishti School", website = "http://srishti.ac.in/"),
        Exam("School of Fashion Technology", website = "www.softpune.com"),
        Exam("Pearl Academy", website = "http://pearlacademy.com"),
        Exam("Symbiosis Institute of Design", website = "http://sid.edu.in/"),
        Exam("Footwear Design and Development Institute", website = "www.fddiindia.com"),
        Exam("Maeer’s MIT Institute of Design", website = "www.mitid.edu.in"),
        Exam("National Institute of Design", website = "www.nid.edu"),
        Exam("National Institute of Fashion Design", website = "www.nift.ac.in"),
        Exam("National Aptitude Test in Architecture", website = "www.nata.in"),
        Exam("CEPT", website = "www.cept.ac.in")
    )

    val languages = listOf(
        Exam("EFLU Entrance Test", "Admission for BA (Hons) languages", "Class 12 / Graduate / PG", "November to January", "Online", "www.efluniversity.ac.in"),
        Exam("JNU Entrance Exam", "Admission in BA (Hons) foreign languages", "Class 12 / Graduate / PG", "February / March", "Online / Post", "www.jnu.ac.in")
    )

    val law = listOf(
        Exam("CLAT", "Admission in BA LL.B (Hons), B.Com LL.B, BBA LL.B", "Class 12", "January to April", "Online", "http://clat.ac.in"),
        Exam("LSAT India", "Admission in law courses", "Class 12 & Graduates", "November to April", "Online", "www.pearsonvueindia.com/lsatindia"),
        Exam("AILET", "Admission in BA LL.B, LLM, PhD", "Class 12 / LL.B / LLM", "February to April", "Online / Post", "www.nludelhi.ac.in"),
        Exam("Lloyd Entrance Test", "Admission in BA LL.B (5-year)", "Class 12", "January to May", "Online / Post", "www.lloydlawcollege.com")
    )

    val humanities = listOf(
        Exam("HSSEE (IIT Madras)", "Integrated MA Programme", "Class 12", "December / January", "Online", "http://hsee.iitm.ac.in"),
        Exam("TISS-BAT", "BA Social Science Programme", "Class 12", "February to April", "Online", "http://campus.tiss.edu")
    )

    val banking = listOf(
        Exam("IBPS-PO", "Probation Officer", "Graduation (60%)", "October / November", "Online", "www.ibps.in"),
        Exam("SBI-PO", "Clerical Recruitment", "Graduation (60%)", "October / November", "Online", "www.sbi.co.in")
    )

    val commerce = listOf(
        Exam("ICAI – CA Entrance Exam", "Chartered Accountancy", "12th / Graduation (60%)", "June / December", "Online", "—"),
        Exam("CMA Foundation Exam", "Cost Accountancy", "12th / Graduation (60%)", "June", "Online", "—")
    )

    val defence = listOf(
        Exam("NDA & NA Examination", "Admission in Army, Air Force, Naval Academy", "12th Class", "—", "Online", "www.upsc.gov.in"),
        Exam("Indian Navy B.Tech Exam", "Admission in Navy B.Tech", "Class 12", "December / January", "Online", "www.nausena-bharti.nic.in"),
        Exam("Indian Navy Sailors Recruitment", "Basic & professional training", "Class 12 (Science)", "—", "Online / Post", "www.nausena-bharti.nic.in"),
        Exam("IMU-CET", "Diploma in Nautical Science", "Class 12 with PCM", "November / December", "By Post", "www.imu.edu.in"),
        Exam("TES (Indian Army)", "Technical Entry Scheme", "Class 12 with PCM", "—", "Online", "www.joinindianarmy.nic.in")
    )

    fun getExams(category: String): List<Exam> {
        return when (category) {
            "Medical" -> medical
            "National Level Medical Entrance Exams" -> nationalMedical
            "Engineering" -> engineering
            "National Level Engineering Entrance Exams" -> nationalEngineering
            "Fashion" -> fashion
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
