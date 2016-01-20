package widok;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jsoup.HttpStatusException;
import org.openqa.selenium.NoSuchElementException;

import obiekty.Kategoria;
import obiekty.Komentarz;
import obiekty.Produkt;
import serwisy.CSV;
import serwisy.Parser;
import serwisy.SerwisCeneo;
import serwisy.SerwisKomentarzy;
import serwisy.SerwisMorele;
import serwisy.SerwisProduktu;

/**
 * Widok określający wygląd aplikacji.
 */
public class Widok {

	/**
	 * Powłoka aplikacji.
	 */
	protected Shell shlPrzegldarkaKomentarzy;

	/**
	 * Pole do wprowadzania id dla wczytywanego produktu.
	 */
	private Text idProduktuText;

	/**
	 * Wprowadzone id produktu.
	 */
	private Integer idProduktu;

	/**
	 * Wczytany produkt.
	 */
	private Produkt sparsowanyProdukt;

	/**
	 * Wczytane komenatrze.
	 */
	private List<Komentarz> sparsowaneKomenatrze;

	/**
	 * Aktualnie wyświetlane produkty.
	 */
	private List<Produkt> wyswietlaneProdukty;

	/**
	 * Aktualnie wyświetlane kategorie.
	 */
	private List<Kategoria> wyswietlaneKategorie;

	/**
	 * Aktualnie wyświetlane komentarze.
	 */
	private List<Komentarz> wyswietlaneKomenatrze;

	/**
	 * Parser
	 */
	private Parser parser = new Parser();

	/**
	 * Serwis dla ceneo
	 */
	private SerwisCeneo serwisCeneo = new SerwisCeneo();

	/**
	 * Serwis dla Moreli
	 */
	private SerwisMorele serwisMorele = new SerwisMorele();

	/**
	 * Pasek postępu
	 */
	private ProgressBar pasekPostepu;

	/**
	 * Przycisk Extract
	 */
	private Button btnExtract;

	/**
	 * Przycisk Transform
	 */
	private Button btnTransform;

	/**
	 * Przycisk Load
	 */
	private Button btnLoad;

	/**
	 * Tabelka wyświetlająca komentarze.
	 */
	private Table table;

	/**
	 * Podgląd tabelki
	 */
	private TableViewer tableViewer;

	/**
	 * Lista zawierająca produkty.
	 */
	private TreeViewer treeViewer;

	/**
	 * Uruchomienie aplikacji.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Widok window = new Widok();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox dialog = new MessageBox(null, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("Unexpected error");

					int returnCode = dialog.open();
				}
			}
		});
	}

	/**
	 * Otwarcie okna aplikacji.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPrzegldarkaKomentarzy.open();
		shlPrzegldarkaKomentarzy.layout();
		while (!shlPrzegldarkaKomentarzy.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Stworzenie elementów graficznych aplikacji.
	 */
	protected void createContents() {
		shlPrzegldarkaKomentarzy = new Shell();
		shlPrzegldarkaKomentarzy.setModified(true);
		shlPrzegldarkaKomentarzy.setSize(900, 500);
		shlPrzegldarkaKomentarzy.setText("Przeglądarka komentarzy");

		idProduktuText = new Text(shlPrzegldarkaKomentarzy, SWT.BORDER);
		idProduktuText.setBounds(10, 10, 76, 21);

		Button btnETL = new Button(shlPrzegldarkaKomentarzy, SWT.NONE);
		btnETL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					extract();
					transform();
					load();
				} catch (NumberFormatException exp) {
					System.out.println("Nieprawidlowe id!");
					MessageBox dialog = new MessageBox(shlPrzegldarkaKomentarzy, SWT.ICON_WARNING | SWT.OK);
					dialog.setText("Info");
					dialog.setMessage("Nieprawidłowe id produktu!");
					int returnCode = dialog.open();
				}
			}
		});
		btnETL.setBounds(92, 10, 75, 25);
		btnETL.setText("ETL");

		pasekPostepu = new ProgressBar(shlPrzegldarkaKomentarzy, SWT.NONE);
		pasekPostepu.setBounds(10, 37, 401, 17);
		serwisCeneo.setProgressBar(pasekPostepu);
		parser.setProgressBar(pasekPostepu);

		btnExtract = new Button(shlPrzegldarkaKomentarzy, SWT.NONE);
		btnExtract.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					extract();
				} catch (NumberFormatException exp) {
					System.out.println("Nieprawidlowe id!");
					MessageBox dialog = new MessageBox(shlPrzegldarkaKomentarzy, SWT.ICON_WARNING | SWT.OK);
					dialog.setText("Info");
					dialog.setMessage("Nieprawidłowe id produktu!");
					int returnCode = dialog.open();
				}
			}
		});
		btnExtract.setBounds(173, 10, 75, 25);
		btnExtract.setText("Extract");

		btnTransform = new Button(shlPrzegldarkaKomentarzy, SWT.NONE);
		btnTransform.setEnabled(false);
		btnTransform.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				transform();
			}
		});
		btnTransform.setBounds(254, 10, 75, 25);
		btnTransform.setText("Transform");

		btnLoad = new Button(shlPrzegldarkaKomentarzy, SWT.NONE);
		btnLoad.setEnabled(false);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				load();
			}
		});
		btnLoad.setBounds(336, 10, 75, 25);
		btnLoad.setText("Load");

		tableViewer = new TableViewer(shlPrzegldarkaKomentarzy, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		createColumns(shlPrzegldarkaKomentarzy, tableViewer);
		tableViewer.setContentProvider(new ArrayContentProvider());
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 127, 864, 302);

		Button btnWyczyBaze = new Button(shlPrzegldarkaKomentarzy, SWT.NONE);
		btnWyczyBaze.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SerwisKomentarzy.usunWszystkieKomentarzeZBazy();
				SerwisProduktu.usunWszystkieProduktyZBazy();

				wyswietlaneKomenatrze = new ArrayList<>();
				wyswietlaneProdukty = new ArrayList<>();
				wyswietlaneKategorie = new ArrayList<>();

				tableViewer.refresh();
				treeViewer.setInput(wyswietlaneKategorie.toArray());
				treeViewer.refresh();

				MessageBox dialog = new MessageBox(shlPrzegldarkaKomentarzy, SWT.ICON_INFORMATION | SWT.OK);
				dialog.setText("Info");
				dialog.setMessage("Wyczyszczono bazę.");

				int returnCode = dialog.open();
			}
		});
		btnWyczyBaze.setBounds(10, 80, 101, 25);
		btnWyczyBaze.setText("Wyczyść baze");

		treeViewer = new TreeViewer(shlPrzegldarkaKomentarzy, SWT.BORDER);
		treeViewer.setContentProvider(new ViewContentProvider());
		treeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Kategoria) {
					Kategoria kategoria = (Kategoria) element;
					return kategoria.getKategoria();
				}
				if (element instanceof Produkt) {
					Produkt produkt = (Produkt) element;
					return "    " + produkt.getId().toString() + " : " + produkt.getMarka() + " " + produkt.getModel()
							+ " (" + produkt.getDodatkoweUwagi() + ")";
				}
				return super.getText(element);
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();

				if (thisSelection.size() > 0) {
					if (thisSelection.getFirstElement() instanceof Produkt) {
						Produkt wybranyProdukt = (Produkt) thisSelection.getFirstElement();
						wyswietlaneKomenatrze = SerwisKomentarzy.pobierzKomentarzeZBazy(wybranyProdukt.getId());
						tableViewer.setInput(getWyswietlaneKomenatrze());
					}
				} else {
					wyswietlaneKomenatrze.clear();
					tableViewer.refresh();
				}
			}
		});
		Tree tree = treeViewer.getTree();
		tree.setBounds(428, 10, 446, 95);

		Button btnZapiszDoCsv = new Button(shlPrzegldarkaKomentarzy, SWT.NONE);
		btnZapiszDoCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (wyswietlaneKomenatrze != null && !wyswietlaneKomenatrze.isEmpty()) {
						CSV.zapiszCSV(wyswietlaneKomenatrze);
						System.out.println("Zapisano.");
						MessageBox dialog = new MessageBox(shlPrzegldarkaKomentarzy, SWT.ICON_INFORMATION | SWT.OK);
						dialog.setText("Info");
						dialog.setMessage("Zapisano.");

						int returnCode = dialog.open();
					}
				} catch (FileNotFoundException e1) {
					System.out.println("Blad zapisu!");
				}
			}
		});
		btnZapiszDoCsv.setBounds(10, 431, 101, 21);
		btnZapiszDoCsv.setText("Zapisz do CSV");
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				if (item.getItemCount() > 0) {
					item.setExpanded(!item.getExpanded());
					treeViewer.refresh();
				}
			}
		});

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);

		setWyswietlaneProdukty(SerwisProduktu.pobierzWszystkieProduktyZBazy());
		stworzDrzewkoProduktow();
	}

	/**
	 * Tworzenie kolumn dla tabelki.
	 * 
	 * @param parent
	 *            Rodzic.
	 * @param viewer
	 *            Podgląd
	 */
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Id", "Autor", "Data", "Podsumowanie", "Gwiazdki", "Opinie", "Rekomendowany", "Zalety",
				"Wady" };
		int[] bounds = { 70, 100, 80, 200, 60, 50, 50, 100, 100 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getId().toString();
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getAutor();
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getData().toString();
			}
		});

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getPodsumowanie();
			}
		});

		col = createTableViewerColumn(titles[4], bounds[4], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getLiczbaGwiazdek().toString();
			}
		});

		col = createTableViewerColumn(titles[5], bounds[5], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getLiczbaPozytywnychOpinii() + "/" + komentarz.getLiczbaOpinii();
			}
		});

		col = createTableViewerColumn(titles[6], bounds[6], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				return komentarz.getPolecany() ? "tak" : "nie";
			}
		});

		col = createTableViewerColumn(titles[7], bounds[7], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				String zalety = "";
				for (String zaleta : komentarz.getZalety()) {
					zalety += zaleta + "; ";
				}
				return zalety;
			}
		});

		col = createTableViewerColumn(titles[8], bounds[8], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Komentarz komentarz = (Komentarz) element;
				String wady = "";
				for (String wada : komentarz.getWady()) {
					wady += wada + "; ";
				}
				return wady;
			}
		});
	}

	/**
	 * Tworzenie pojedyńczej kolumny.
	 * 
	 * @param title
	 *            Tytuł.
	 * @param bound
	 *            Szerokość.
	 * @param colNumber
	 *            Numer kolumny.
	 * @return Kolumna
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Pobranie stron.
	 */
	private void extract() {
		idProduktu = Integer.parseInt(idProduktuText.getText());
		System.out.println("Pobieranie stron z Ceneo...");
		pasekPostepu.setSelection(0);
		new Thread() {
			public void run() {
				try {
					serwisCeneo.pobierzStroneProduktu(idProduktu);
					serwisCeneo.pobierzStronyZKomentarzami(idProduktu);
					System.out.println("Pobrano strony z Ceneo.");
					System.out.println("Pobieranie stron z Morele...");
					serwisMorele.pobierzStronyZKomentarzami(idProduktu);
					System.out.println("Pobrano strony z Morele.");
					btnTransform.setEnabled(true);
					btnLoad.setEnabled(false);
				} catch (HttpStatusException e) {
					System.out.println("Error: brak produktu na Ceneo.");
					MessageBox dialog = new MessageBox(shlPrzegldarkaKomentarzy, SWT.ICON_WARNING | SWT.OK);
					dialog.setText("Info");
					dialog.setMessage("Brak produktu o podanym id.");
					int returnCode = dialog.open();
				} catch (NoSuchElementException e) {
					System.out.println("Error: brak produktu na Morele.net.");
					btnTransform.setEnabled(true);
					btnLoad.setEnabled(false);
				} catch (IOException e1) {
					System.out.println("Error: Błąd pobierania stron.");
				} catch (Exception e) {
					System.out.println("Error: Błąd pobierania stron.");
				}
				pasekPostepu.setSelection(0);
			}
		}.run();
	}

	/**
	 * Przekształcenie stron i wczytanie komentarzy
	 */
	private void transform() {
		pasekPostepu.setSelection(0);
		new Thread() {
			public void run() {
				try {
					sparsowanyProdukt = parser.parsujProduktZCeneo(serwisCeneo.getStronaZProduktem(), idProduktu);
					System.out.println("Parsowanie stron z Ceneo...");
					sparsowaneKomenatrze = parser.parsujKomentarzeZCeneo(serwisCeneo.getStronyZKomentarzami());
					System.out.println("Parsowanie stron z Morele...");
					sparsowaneKomenatrze.addAll(Parser.parsujKomentarzeZMorele(serwisMorele.getStronyZKomentarzami()));
					System.out.println("Parsowanie zakonczone.");
					btnExtract.setEnabled(false);
					btnLoad.setEnabled(true);
				} catch (ParseException e) {
					System.out.println("Error: parsowanie stron.");
				}
				pasekPostepu.setSelection(0);
			}
		}.run();
	}

	/**
	 * Zapis komentarzy do bazy danych.
	 */
	private void load() {
		pasekPostepu.setSelection(0);
		new Thread() {
			public void run() {
				System.out.println("Zapis do bazy...");
				Produkt produkt = SerwisProduktu.pobierzProduktZBazy(idProduktu);
				if (produkt == null) {
					SerwisProduktu.zapiszProdukt(sparsowanyProdukt);
				}
				setWyswietlaneProdukty(SerwisProduktu.pobierzWszystkieProduktyZBazy());

				int liczbaSparsowanychKomentarzy = sparsowaneKomenatrze.size();
				List<Komentarz> komentarzeWBazie = SerwisKomentarzy.pobierzKomentarzeZBazy(idProduktu);
				sparsowaneKomenatrze.removeAll(komentarzeWBazie);
				if (!sparsowaneKomenatrze.isEmpty()) {
					SerwisKomentarzy.zapiszKomentarze(sparsowanyProdukt, sparsowaneKomenatrze);
				}
				pasekPostepu.setSelection(0);

				komentarzeWBazie = SerwisKomentarzy.pobierzKomentarzeZBazy(idProduktu);
				setWyswietlaneKomenatrze(komentarzeWBazie);

				stworzDrzewkoProduktow();

				System.out.println("Zapis do bazy zakonczony.");
				MessageBox dialog = new MessageBox(shlPrzegldarkaKomentarzy, SWT.ICON_INFORMATION | SWT.OK);
				dialog.setText("Info");
				dialog.setMessage("Wczytane strony: " + (serwisCeneo.getStronyZKomentarzami().size() + 1) + "\n"
						+ "Sparsowane komentarze: " + liczbaSparsowanychKomentarzy + "\n" + "Zapisane rekordy: "
						+ sparsowaneKomenatrze.size());

				int returnCode = dialog.open();
				btnExtract.setEnabled(true);
				btnTransform.setEnabled(false);
				btnLoad.setEnabled(false);
			}
		}.run();
	}

	/**
	 * Tworzenie danych dla listy produktów.
	 */
	private void stworzDrzewkoProduktow() {
		Map<String, List<Produkt>> kategoriaProduktMap = new HashMap<>();
		for (Produkt wyswietlanyProdukt : getWyswietlaneProdukty()) {
			if (!kategoriaProduktMap.containsKey(wyswietlanyProdukt.getRodzaj())) {
				kategoriaProduktMap.put(wyswietlanyProdukt.getRodzaj(), new ArrayList<Produkt>());
				kategoriaProduktMap.get(wyswietlanyProdukt.getRodzaj()).add(wyswietlanyProdukt);
			} else {
				kategoriaProduktMap.get(wyswietlanyProdukt.getRodzaj()).add(wyswietlanyProdukt);
			}
		}

		wyswietlaneKategorie = new ArrayList<>();
		for (Entry<String, List<Produkt>> entry : kategoriaProduktMap.entrySet()) {
			Kategoria kategoriaWrapper = new Kategoria();
			kategoriaWrapper.setKategoria(entry.getKey());
			kategoriaWrapper.setProdukty(entry.getValue());
			for (Produkt produktWrapper : entry.getValue()) {
				produktWrapper.setParent(kategoriaWrapper);
			}
			wyswietlaneKategorie.add(kategoriaWrapper);
		}
		treeViewer.setInput(getWyswietlaneKategorie().toArray());
	}

	public List<Komentarz> getWyswietlaneKomenatrze() {
		return wyswietlaneKomenatrze;
	}

	public void setWyswietlaneKomenatrze(List<Komentarz> wyswietlaneKomenatrze) {
		this.wyswietlaneKomenatrze = wyswietlaneKomenatrze;
	}

	public List<Produkt> getWyswietlaneProdukty() {
		return wyswietlaneProdukty;
	}

	public void setWyswietlaneProdukty(List<Produkt> wyswietlaneProdukty) {
		this.wyswietlaneProdukty = wyswietlaneProdukty;
	}

	public List<Kategoria> getWyswietlaneKategorie() {
		return wyswietlaneKategorie;
	}

	public void setWyswietlaneKategorie(List<Kategoria> wyswietlaneKategorie) {
		this.wyswietlaneKategorie = wyswietlaneKategorie;
	}

	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			Kategoria kategoria = (Kategoria) parentElement;
			return kategoria.getProdukty().toArray();
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Kategoria) {
				Kategoria kategoria = (Kategoria) element;
				if (!kategoria.getProdukty().isEmpty()) {
					return true;
				}
			} else if (element instanceof Produkt) {
				return false;
			}
			return false;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof Kategoria) {
				return null;
			}
			Produkt produkt = (Produkt) element;
			return produkt.getParent();
		}

	}
}
