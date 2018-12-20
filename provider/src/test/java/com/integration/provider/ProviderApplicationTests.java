package com.integration.provider;

import com.integration.provider.domain.DictionaryResult;
import com.integration.provider.manager.DictionaryTreeManager;
import com.integration.provider.manager.MapManager;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderApplicationTests {

    @Test
    public void DictionaryTree() {
        String str = "i have a dream copyright 1963 martin ltrther king  jr speooh by the rev martin luther king at the  march on vyashington  i am happy to join with you today in what will go down in history as the greatest demonstration for freedom in the history of our nation five core years ago a great american in whose symholic shadow we stand today signed the emancipation proclamation this momentous cleeree is a great beacon light of hope to millions of negro slaves who had been earrd in the flames o withering injushcc it came as a joyous daybre ak to end the long night of their captivity but 100 years later the negro still is not free one hundred years later the 1ife of the negro is still badly erippled by the manacles of stgrpgation and the chains of discriminatio11 one hnndred years later the ne gro lives on a  lone jy i and of poverty in the mids1t of a va1s1t ooean of material prospcrity out hundred years later the egro is still laruguihed iu the comers of american oci ety and find himself in exile in his own land so wo come hfrp torlay to r1ramatize a  s hameful condition in a sense weve come to our nations capital to cash a eheck whrn the a rebit ects of our republie wrote the magnificent wonls of the constitution and the declaration o1 independence  they were signing a promissclry note to which every american wa  to fall heir ihis no te was a promise that all nwnyc  bla ek men as we11 as white rmmwonld he g1mnmtptd the unalienable rights of life  liherty all the pnrsni1 of happiness it is obvious today that amcria lms lcfaulted on this promissory note insofar as hpr citi7ens of cojo r arr concprned instead of  2  honoring ih1s sacnd ohli gation  america has given the nngro p0ople a bad check  a check which has come back marked insufficient fnnds but we refuse to helieve that the bank of justice is bankrupt we rpfnse to belie that there are insufficient fuwls in the great vaults of opportunity orf this nation so weve come to cash this check  a check that will give ns upon oemand the rirhes of freedom and the security of justice ve haye abo come to this hallowed spot to remind america of the fipjce urgency of now lhi is no time to lllgagc in the luxury of cooling off or to take the tranquilizing dmg of gradualim now is the time to make real the promirs of democracy now is the time to rise from the dark nnll clrsolate valley of segregation to the  unlit path of racial justip now is the time to lift our uation from the quicksands of raeial injustice to the solid rek of brotherhood xow is the time to mak0 justice a reality for all of gods children it would be fatal for the nation to overlook the urgency of the moment this sweltering summer of the iegros legitimate discontent will not pass until there is an invigorating autumn of freedom and equality 1963 is not an md but a beginning those who hope that tl1e xegro needed to hlow off steam and will now be lolltent will hae a ruop awakening if the nation return11 to bu ine as usuai lhere will be 11eithtt rest nor tranquility in america until the negro i granted his citizenship rights the whirlwinds of revolt will continue to shake the foundations of onr nation until the bright days of justice emerge copyright 101i3  marti ltthfr kikc  jr  3  and that is something that i  must say to my pbople who tand on the worn threshold whieh leads into the palace of justice in the process of gaining our rightful place we must not he guilty of wrongful deeds let us not  eek to satisfy our thirst for freedom by drinking from the cup of bimenles s  and hatred ve must forever conduct our struggle on the high plane of dignity and diseiplin e ye must not allow our creative protests to degen erate into physical violence again and again we mus t  ri se to the maje stic heights of meding physical force with soul force rlhe marvelous new militancy which has cngulfed the negro community must not lead us to distrust all white people  for many o our white hrotlwrs  as evidcnccj by their presence here today  have eome to realize that their destiny is tied up with our detiny they have come to realize that their freedom is inexttieably hound to o1r freroom e cannot walk alone and as we walk we must make the pledge that we shall always march ahcarl we cannot turn hack there are those who are asking the rlevotees of civil rights   when will you be satisfied 1 ve can never be satisfied as lmg ns the negro i the victim of the unspeakable horrors of police brutality !e  can never be satisfied as long as our bodie s  heavy with the fatigue of travel  cannot gain lodging in the motels of the highways and the hotels of the cities ve cannot be satisfied as long as the negros basic mobility is from a smaller ghetto to a larger one we an never be satisfiel as long as our children are stripped of their adulthood and robbed of their dignity by s1gns tating for tj1ites only   copyright 1963 far ti! luthfr king  jr  4  we oannot be satisfied 3ls long as the negro in mississippi cannot vote and the negro in new york believes he has nothing for which to vote no  no  we are not satisfied  and we will noit be satisfied until justice rolls down like waters and righteousness like a mighty stream i am not unmindful that some of you have come here out of great trials and tribulation some of you have eome fresh from narrow jail !ells s ome of you have oome from areas where your quest for freedom left you battered by the storms of persecution and staggered by 1hf winds of police brutality you have been the veterans of erea tive suffering continue to work with the faith that unearned suffering is redemptive go hack to mississippi  go back to alaharna  go back to south carolina  go back to georgia  go hack to louisiana  go back to the slums and ghettos of our  orthern cities  knowing that somehow this situation oan and will be changed let us not wallow in the valley ot despair  i say to you today  my friends  though  even though we face the difficultes of today and tomorrow  i still have a dream it is a dream deeply rooted in the american dleam i have a dream that one day this 11ation will rise up  live out the hue meaning of its creed    e hold thee truths to be selfevident  that all men are created equal  i have a dream that one day on the red hills of georgia son of former slan s and tlw ons of formlr laveowners will be able to sit dovn together at the table of brotherhood i have a dream that one day even the state of jlisissippi  a state sweltering with the  h eat of injustice  copyright 19ol marti1 luther king  jr  5  liweltering with the heat of oppression  will be trans!ormed into an oasis of freedom and justice i have a dream that my four little chi1dre!il will one day live in a nation vhere they will not be judged by the color of their skin but by the content of the ir chara{!te rl i have a dream  i have a dream tha t one day in aj abama  with its vicious racists  with its governor having his lips dripping with the words of interposition and nullification  one day right tlere in alabama little black boys and black gids will he abl e  to join hands with little white boys and white girls as sisters anrl brothers i have a dream today  i have a dream that one day every vauey shall be exalted  eey hill and mountain ohall be made lov lhe rough places will be made plain  aad the crooked ijlaces will be made straight  and the lory of the lord shall be revealed  and all flesh shall see it together this is onr hope this is the faith that i go back to thc south with with this faith we will be ahh to hew out of the mountain of despair a stone of hope with this faith we   ill he ahle to transform the jangling discords of our nation into a beautiful symphony f brotherhood with this faith we will be able to work togother  to pray together  to struggle together  to go to jail togethn  to stand up for freedom together  knowing that we will he fne one day this will ih the clay when all of gods children will be nble to sing with new meaning  my country  tis of thee  weot land of libtlty  of thee i ing land where my fathers died  land of th pilgrim  s pride  from every mountain side  let freedom ring and if america is to he a great nation  this mnt  become true so let freedom ring from tlh prodigious hilltops of new hampshire let freedom ring from the mighty mountains of new copyright 1963  martin lunrer king   jr  6  york let freooom ring from the heightening alleghenies of pennsylvania let freedom ring from thb snowcapped rookies of colorado let freedom ring from the curvaeeous slopes of california but not only that let freedom ring from stone mountain of georgia let freedom ring from lookout mountain of tennessee let freedom ring from every hill and molehill of mssissippi  from every mountain side let freedom ring      when we allow freedom to ringwhen we let it ring from every city and eyery hamlet  from every state and every city  we will be able to speed up that day when all if gods children  black men and white men  jews and gntiles  protbstants and catholics  will be able to join hands and sing in the words of the old negro spiritual   fiee at last  free at last  great god amighty   ve are free at last  copyright 1963  martin luther king  jr ";
        DictionaryTreeManager manager = new DictionaryTreeManager();
        String[] words = str.split(" +");
        Set<String> set = new HashSet<>();
        for (String word : words) {
            manager.addWord(word);
            set.add(word);
        }
        Assert.assertEquals(set.size(), manager.count());
        Assert.assertTrue(manager.checkSelf());

        DictionaryResult result = manager.findWordWithPrefix("t");
        Assert.assertEquals(result.getTopResultList().size(), 10);
        Assert.assertEquals(result.getTotalCount(), 49);
        Assert.assertEquals(manager.getFindTime(), 17);

        result = manager.findWordWithPrefix("to");
        Assert.assertEquals(result.getTopResultList().size(), 7);
        Assert.assertEquals(result.getTotalCount(), 7);
        Assert.assertEquals(manager.getFindTime(), 11);

        result = manager.findWordWithPrefix("tog");
        Assert.assertEquals(result.getTopResultList().size(), 3);
        Assert.assertEquals(result.getTotalCount(), 3);
        Assert.assertEquals(manager.getFindTime(), 8);

        result = manager.findWordWithPrefix("toge");
        Assert.assertEquals(result.getTopResultList().size(), 2);
        Assert.assertEquals(result.getTotalCount(), 2);
        Assert.assertEquals(manager.getFindTime(), 8);

        result = manager.findWordWithPrefix("togeth");
        Assert.assertEquals(result.getTopResultList().size(), 2);
        Assert.assertEquals(result.getTotalCount(), 2);
        Assert.assertEquals(manager.getFindTime(), 7);

        result = manager.findWordWithPrefix("together");
        Assert.assertEquals(result.getTopResultList().size(), 1);
        Assert.assertEquals(result.getTotalCount(), 1);
        Assert.assertEquals(manager.getFindTime(), 6);

        result = manager.findWordWithPrefix("togethers");
        Assert.assertEquals(result.getTopResultList().size(), 0);
        Assert.assertEquals(result.getTotalCount(), 0);
        Assert.assertEquals(manager.getFindTime(), 6);
    }

    @Test
    public void Map() {
        MapManager mapManager = new MapManager();
        Random r = new Random(20181220);
        for (int i = 0; i < 100000; ++i) {
            long x = r.nextInt(10000) - 5000;
            long y = r.nextInt(10000) - 5000;
            mapManager.AddPoint(x, y);
        }
        Assert.assertEquals(mapManager.getPointCount(), 99953);
        MapManager.Point p = mapManager.findNearest(0, 0);
        Assert.assertEquals(p.getX(), 3);
        Assert.assertEquals(p.getY(), -10);
    }
}
