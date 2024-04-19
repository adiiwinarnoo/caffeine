package com.restaurant.caffeinapplication.data.model

data class TestModel(var Tittle : String)
data class StockProduct(var title : String, var stock : Int)
data class OptionProduct(var title : String)
data class OptionProductT(var title : String)

data class Group( // Jenis grup (misalnya: 0, 1, 2)
    val title: String, // Judul grup
    val totalIncome : String,
    val children: List<String>, // Children untuk tipe grup 0 dan 1
    val childrenIncomeDate: List<String>, // Children untuk tipe grup 0 dan 1
    var recyclerViewItem: List<ChildItem>, // Children untuk tipe grup 2
    var isExpanded: Boolean = false,
)
data class GroupIncomeOrder( // Jenis grup (misalnya: 0, 1, 2)
    val title: String, // Judul grup
    val totalIncome : String,
    val children: List<String>, // Children untuk tipe grup 0 dan 1
    val childrenIncomeDate: List<String>, // Children untuk tipe grup 0 dan 1
    var recyclerViewItem: List<ChildItemIncomeOrder>, // Children untuk tipe grup 2
    var isExpanded: Boolean = false,
)

data class GroupIncomeCoupon( // Jenis grup (misalnya: 0, 1, 2)
    val title: String, // Judul grup
    val totalIncome : String,
    val children: List<String>, // Children untuk tipe grup 0 dan 1
    val childrenIncomeDate: List<String>, // Children untuk tipe grup 0 dan 1
    var recyclerViewItem: List<ChildItemIncomeCoupon>, // Children untuk tipe grup 2
    var isExpanded: Boolean = false,
)

data class GroupIncomeSubscribe( // Jenis grup (misalnya: 0, 1, 2)
    val title: String, // Judul grup
    val totalIncome : String,
    val children: List<String>, // Children untuk tipe grup 0 dan 1
    val childrenIncomeDate: List<String>, // Children untuk tipe grup 0 dan 1
    var recyclerViewItem: List<ChildItemIncomeSubscribe>, // Children untuk tipe grup 2
    var isExpanded: Boolean = false,
)

data class YearMonthDate(val year: String, val month: String, val date: String)

