package neoe.ime.cn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import neoe.ime.ImeUnit;

public class PyResearch {

	public static void main(String[] args) throws Exception {
		CnCharLib cl = new CnCharLib();
		cl.getInitThread().join();

		System.out.println(cl.m.data.size());

//		System.out.println(ks );
		for (Object o : notused) {
			cl.m.data.remove(o);
		}
		System.out.println(cl.m.data.size());
		int cnt = 0;
		List res = new ArrayList();
		Set ks = cl.m.data.keySet();
		for (Object k : ks) {
			List v = (List) cl.m.data.get(k);
			if (v.size() >= 3) {
				System.out.println(k + ":" + v.size());
				cnt++;
			}
			res.add(new Object[] { k, v });
		}
		System.out.println("total=" + cnt);
		Collections.sort(res, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				List l1 = (List) ((Object[]) o1)[1];
				List l2 = (List) ((Object[]) o2)[1];
				int i1 = l1.size();
				int i2 = l2.size();
				if (i1 == i2)
					return 0;
				if (i1 > i2)
					return -1;
				return 1;
			}
		});
		int sum2 = 0;
		for (Object o : res) {
			Object[] row = (Object[]) o;
			List v = (List) row[1];
			sum2 += v.size();
			System.out.printf("%s %s\n", row[0], /* v.size() */ v.toString());
		}
		System.out.println(res.size());
		System.out.println(sum2);
		testText1(cl);
	}

	private static void testText1(CnCharLib cl) throws IOException {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		CnCharOneLib one = new CnCharOneLib();
		for (char c : text1.toCharArray()) {
			String py = cl.reverse(c);
			sb.append(py).append(" ");
			String rev = one.getChar(py);
			if (rev == null) {
				sb2.append(c);
				// System.out.println("miss " + py + "," + c);
			} else {
				sb2.append(rev);
			}
		}
		System.out.println(sb);
//		System.out.println(text1);
		System.out.println(sb2);
	}

	static String text1 = "游戏植入了大量真实的细节，玩家在游戏中能体验四季轮回、昼夜交替和风霜雨雪，而游戏人物不仅仅只是冰冷空洞的资源生产工具，每个人物都具备独特和复杂的背景故事，会有各自的喜好和需求，会产生复杂的情绪。玩家必须要合理应对人物的需求和情绪才能保证模拟经营的良好运转，在玩家和人物频繁的互动中，玩家和人物建立了情感连接，代入感更加强烈。";
	// "早在清末的切音字运动中，就已经开始考虑到拼音正词法问题。卢戆章在他的《一目了然初阶》一书中，已经用短横连接音节的方法实行了初步的分词连写。蔡锡勇的《传音快字》一书中，提出了“连书”的概念。";

	static String[] notused = { "nou", "uu", "kei", "den", "n", "ppun", "liwa", "ceok", "ra", "keo", "yen", "tae",
			"nve", "seng", "dem", "fiao", "phas", "ram", "teo", "be", "hol", "sed", "sei", "seo", "sen", "heui", "zhei",
			"m", "kwi", "dia", "keol", "dim", "keos", "pak", "ei", "eo", "yug", "cis", "aes", "eng", "go", "bia",
			"shiwa", "teun", "teul", "eol", "ho", "eos", "hwa", "keum", "tol", "saeng", "ton", "oes", "ki", "mas",
			"fui", "lo", "sol", "nen", "nem", "meo", "kal", "ol", "on", "myeon", "phoi", "phos", "ceor", "ceom", "ceon",
			"nung", "dug", "dul", "kweok", "zad", "so", "hal", "qianke", "neus", "peol", "phdeng", "qianwa", "mol",
			"jialun", "mangmi", "zo", "gei", "fenwa", "geu", "myeong", "myeo", "seon", "nun", "shike", "kos", "ngai",
			"ngam", "haoke", "tap", "ngag", "gib", "gongli", "cal", "jou", "wie", "sal", };

}
