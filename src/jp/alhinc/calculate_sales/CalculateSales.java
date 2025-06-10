package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
// --change--商品定義の追加--2025/6/10--
//	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_NOT_EXIST = "が存在しません";
// --change--商品定義の追加--2025/6/10--
//	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";
	private static final String FILE_INVALID_FORMAT = "のフォーマットが不正です";
	private static final String SALES_FILE_NOT_SERIAL = "売上ファイル名が連番になっていません";
	private static final String EXCESSIVE_DIGIT = "合計金額が10桁を超えました";
	private static final String BRANCH_CODE_NOT_EXIST = "の支店コードが不正です";
	private static final String SALES_FILE_INVALID_FORMAT = "のフォーマットが不正です";

// --add--商品定義の追加--2025/6/10--
	private static final String FILE_NAME_COMMODITY_LST = "commodity.lst";
	private static final String BRANCH_DEFINITION_FILE = "支店定義ファイル";
	private static final String COMODITY_DEFINITION_FILE = "商品定義ファイル";
	private static final String BRANCH_CODE_FORMAT = "^[0-9]{3}$";
	private static final String COMODITY_CODE_FORMAT = "\"^[0-9a-zA-Z]{8}$\"";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// コマンドライン引数の確認
		if(args.length != 1) {
			System.out.println(UNKNOWN_ERROR);
			return;
		}

		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

// --add--商品定義の追加--2025/6/10--
		// 支店コードと支店名を保持するMap
		Map<String, String> commodityNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> commoditySales = new HashMap<>();

		// 支店定義ファイル読み込み処理
// --change--商品定義の追加--2025/6/10--
//		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales,
					BRANCH_DEFINITION_FILE, BRANCH_CODE_FORMAT)) {
			return;
		}

// --add--商品定義の追加--2025/6/10--
		// 商品定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_COMMODITY_LST, commodityNames, commoditySales,
					COMODITY_DEFINITION_FILE, COMODITY_CODE_FORMAT)) {
		return;
	}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		// 売上ファイル集計処理
		if(!aggregateSalesFile(args[0], branchNames, branchSales)) {
			return;
		}

		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

// --change--商品定義の追加--2025/6/10--
//	/**
//	 * 支店定義ファイル読み込み処理
//	 *
//	 * @param フォルダパス
//	 * @param ファイル名
//	 * @param 支店コードと支店名を保持するMap
//	 * @param 支店コードと売上金額を保持するMap
//	 * @return 読み込み可否
//	 */
//	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
	/**
	 * 定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 名称保持用Map
	 * @param 金額保持用Map
	 * @param 定義ファイル種別
	 * @param コード形式
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> namesMap,
					Map<String, Long> salesMap, String definitionFile, String codeFormat) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);

			// 定義ファイルの存在確認
			if(!file.exists()) {
// --change--商品定義の追加--2025/6/10--
//				System.out.println(FILE_NOT_EXIST);
				System.out.println(definitionFile + FILE_NOT_EXIST);
				return false;
			}

			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)

				// 読み取った一行のデータを「,」(カンマ)で項目ごとに分割
// --change--商品定義の追加--2025/6/10--
//				String[] branchInformation = line.split(",");
				String[] readingInformation = line.split(",");

				// 定義ファイルのフォーマット確認
// --change--商品定義の追加--2025/6/10--
//				if((branchInformation.length != 2) || (!branchInformation[0].matches("^[0-9]{3}$"))) {
//					System.out.println(FILE_INVALID_FORMAT);
				if((readingInformation.length != 2) || (!readingInformation[0].matches(codeFormat))) {
					System.out.println(definitionFile + FILE_INVALID_FORMAT);
					return false;
				}

				// 名称保持用Mapに値を代入
// --change--商品定義の追加--2025/6/10--
//				branchNames.put(branchInformation[0], branchInformation[1]);
				namesMap.put(readingInformation[0], readingInformation[1]);

				// 金額保持用Mapに値を代入
// --change--商品定義の追加--2025/6/10--
//				branchSales.put(branchInformation[0], (long) 0);
				salesMap.put(readingInformation[0], (long) 0);

				System.out.println(line);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 売上ファイル集計処理
	 *
	 * @param フォルダパス
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean aggregateSalesFile(String path, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// 指定パス内の全てのファイル情報を取得
		File[] allFiles = new File(path).listFiles();

		List<File> rcdFiles = new ArrayList<>();

		// 売上ファイルの抽出
		for(int i = 0; i < allFiles.length; i++) {
			if(allFiles[i].isFile() && allFiles[i].getName().matches("^[0-9]{8}\\.rcd$")) {
				rcdFiles.add(allFiles[i]);
			}
		}

		// 売上ファイルのソート
		Collections.sort(rcdFiles);

		// 売上ファイルのファイル名連番確認
		for(int i = 0; i < (rcdFiles.size() - 1); i++) {
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));

			if((latter - former) != 1) {
				System.out.println(SALES_FILE_NOT_SERIAL);
				return false;
			}
		}

		// 支店ごとに売上を合算
		for(int i = 0; i < rcdFiles.size(); i++) {
			BufferedReader br = null;

			try {
				FileReader fr = new FileReader(rcdFiles.get(i));
				br = new BufferedReader(fr);

				String line;
				List<String> salesFileItems = new ArrayList<>();

				// 支店コード、売上金額の取得
				while((line = br.readLine()) != null) {
					salesFileItems.add(line);
				}

				//売上ファイルのフォーマット確認
				if(salesFileItems.size() != 2) {
					System.out.println("「" + rcdFiles.get(i).getName() + "」" + SALES_FILE_INVALID_FORMAT);
					return false;
				}

				// 支店コードの存在確認
				if(!branchNames.containsKey(salesFileItems.get(0))) {
					System.out.println("「" + rcdFiles.get(i).getName() + "」" + BRANCH_CODE_NOT_EXIST);
					return false;
				}

				// 売上金額が数字であるかの確認
				if(!salesFileItems.get(1).matches("^[0-9]{1,}$")) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}

				Long salesAmount = Long.parseLong(salesFileItems.get(1));

				// 売上金額の合算処理
				Long totalAmount = branchSales.get(salesFileItems.get(0)) + salesAmount;

				// 売上合計金額の桁数確認
				if(totalAmount >= 10000000000L) {
					System.out.println(EXCESSIVE_DIGIT);
					return false;
				}

				// 売上金額を売上情報用Mapに反映
				branchSales.put(salesFileItems.get(0), totalAmount);

			} catch (IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return false;
			} finally {
				if(br != null) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		File file = new File(path, fileName);

		BufferedWriter bw = null;

		try {
			FileWriter fw = new FileWriter(file, true);

			bw = new BufferedWriter(fw);

			// 集計結果を支店別集計ファイルに出力
			for(String branchNumber: branchNames.keySet()) {
				bw.write(branchNumber + "," + branchNames.get(branchNumber) + "," + branchSales.get(branchNumber));
				bw.newLine();
			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println(UNKNOWN_ERROR);

			return false;
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);

					return false;
				}
			}
		}

		return true;
	}

}
