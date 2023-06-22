package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Controller;

public class News extends Controller {
	private static Logger log = Logger.getLogger(News.class);

	public static void latest() {
		log.debug("latest. ");

		List<models.News> news = new ArrayList<models.News>();
		news.add(new models.News(1, "BP, Toyota, Nokia, Masuk 'Daftar Perusahaan Paling Dibenci di AS'", "<p>Siapa saja perusahaan-perusahaan yang paling dibenci di Amerika? Terdapat 15 perusahaan masuk dalam daftar paling dibenci di AS selama tahun 2010.</p>" + "<p>Mereka dibenci oleh para konsumen, karyawan, pemegang saham dan para pembayar pajak di AS karena sejumlah alasan. Dan website 24/7 WallSt melakukan survei untuk mencari siapa saja yang paling dibenci masyarakat AS.</p>" + "Untuk menentukan daftar '15 Perusahaan Paling Dibenci AS di tahun 2010', website tersebut mengevaluasi sejumlah perusahaan berdasarkan 6 kriteria.", new Date(), "user1"));
		news.add(new models.News(2, "Industri Was-was Jaminan Pasokan Gas di 2011", "<p>Para pelaku industri pengguna gas mengaku merasa was-was dengan jaminan pasokan gas di 2011. Meskipun kontrak suplai gas untuk industri banyak berakhir di 2012 namun bulan Maret 2011 ada beberapa industri yang akan berakhir kontraknya.</p>" + "<p>\"Para industri mulai deg-degan mendapatkan jaminan pasokan gas yang benar,\" kata Ketua Umum Forum Industri Pengguna Gas Bumi (FIPGB) Achmad Safiun di kantor Kementerian Perindustrian, Jakarta, Jumat (7/1/2011).</p>" + "<p>Ia mengatakan jaminan pasokan gas bagi industri terutama dari PGN dan pemerintah (BP Migas) akan menentukan pertumbuhan industri di 2011. Jika ini tidak terjamin maka imbasnya akan buruk yaitu sampai pada penutupan industri atau bahkan pelarian modal ke luar negeri yang berujung pada penambahan pengangguran.</p>" + "<p>Sementara itu Sekjen FIPGB Achmad Widjaja mengatakan persoalan pasokan gas ke Industri bukan masalah baru namun selalu muncul setiap tahun. Ia menilai pemerintah seperti tak memiliki keberpihakan pada industri nasional.</p>" + "<p>\"Kita mempertanyakan keputusan politik pemerintah. Ada data pada Mei 2010 ada 7 menteri untuk tak memotong 20% pasokan gas, faktanya ini tak terealisasi,\" tegas Widjaja.</p>", new Date(), "user1"));
		render(news);
	}
}
